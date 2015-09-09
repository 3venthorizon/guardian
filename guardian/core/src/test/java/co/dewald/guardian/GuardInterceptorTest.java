package co.dewald.guardian;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import javax.interceptor.InvocationContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import co.dewald.guardian.gate.Grant;
import co.dewald.guardian.gate.Guardian;
import co.dewald.guardian.session.Session;


/**
 * @see {@link GuardInterceptor#grant(javax.interceptor.InvocationContext)}
 * 
 * @author Dewald Pretorius
 */
@RunWith(MockitoJUnitRunner.class)
public class GuardInterceptorTest {
    static final String ERROR = "Illegal call on stack";
    
    @Mock Guardian guardian;
    
    class PartialInterceptor extends GuardInterceptor {
        int step = 0;
    
        public PartialInterceptor() {
            session = new Session();
            session.setUsername("username");
        }
        
        boolean authorised = false;
        int authoriseStep = 0;
        String authUser = null;
        String authResource = null;
        String authAction = null;

        @Override
        void authorise(String username, String resource, String action) {
            step++;
            authoriseStep = step;
            authUser = username;
            authResource = resource;
            authAction = action;
            
            if (!authorised) throw new SecurityException("Unauthorised access");
        }
        
        int parameterStep = 0;
        InvocationContext parameterContext = null;
        String parameterUser = null;
        
        @Override
        void filterParameters(InvocationContext ctx, String username) {
            step++;
            parameterStep = step;
            parameterContext = ctx;
            parameterUser = username;
        }
        
        int resultStep = 0;
        InvocationContext resultContext = null;
        String resultUser = null;
        Object result = null;

        @Override
        Object filterResult(InvocationContext ctx, String username) throws Exception {
            step++;
            resultStep = step;
            resultContext = ctx;
            resultUser = username;
            return result;
        }
    }
    
    class GuardContext implements InvocationContext {
        Method method;
        Object proceedResult;
        
        int proceedCount = 0;
        int methodCount = 0;
        
        @Override
        public Method getMethod() {
            methodCount++;
            return method;
        }
        
        @Override
        public Object proceed() throws Exception {
            proceedCount++;
            return proceedResult;
        }

        //formatter:off
        @Override
        public Object getTarget() { throw new RuntimeException(ERROR); }
        @Override
        public Object getTimer() { throw new RuntimeException(ERROR); }
        @Override
        public Constructor<?> getConstructor() { throw new RuntimeException(ERROR); }
        @Override
        public Object[] getParameters() { throw new RuntimeException(ERROR); }
        @Override
        public void setParameters(Object[] params) { throw new RuntimeException(ERROR); }
        @Override
        public Map<String, Object> getContextData() { throw new RuntimeException(ERROR); }
        //formatter:on
    }
    
    //formatter:off
    @Grant(name = "CheckFalse", check = false)
    class CheckFalse {
        @Grant(name = "checkfalse", check = false)
        public void checkFalse() {}
    }
    
    @Grant(name = "CheckTrue")
    class CheckTrue {
        @Grant(name = "checktrue")
        public void checkTrue() {}
    }
    
    @Grant(name = "CheckFalse", check = false)
    class ResourceCheckFalse {
        @Grant(name = "checktrue")
        public void checkTrue() {}
    }
    
    @Grant(name = "CheckTrue")
    class ActionCheckFalse {
        @Grant(name = "checkfalse", check = false)
        public void checkFalse() {}
    }
    
    class UnMarked {
        public void action() {}
    }
    
    @Grant(check = true)
    class NoName {
        @Grant(check = true)
        public void nothing() {}
    }
    
    @Grant(name = "ResourceFilter", filter = true)
    class ResourceFilter {
        public void filter(CheckTrue checkTrue) { }
    }
    
    @Grant(name = "ActionFilter", filter = false)
    class ActionFilter {
        @Grant(name = "action", filter = true) //overrides type filter
        public void filter(CheckTrue checkTrue) { }
    }
    //formatter:on
    
    PartialInterceptor interceptor;
    GuardContext context;
    
    @Before
    public void setUp() {
        interceptor = new PartialInterceptor();
        interceptor.guardian = guardian;
        context = new GuardContext();
    }
    
    @Test(expected = SecurityException.class)
    public void disabled() throws Exception {
        when(guardian.checkState(anyString(), anyString())).thenThrow(new SecurityException("Deactivated Resource"));
        context.method = CheckFalse.class.getDeclaredMethod("checkFalse", (Class[]) null);
        
        try {
            interceptor.grant(context);
        } catch(SecurityException se) {
            assertEquals(0, context.proceedCount);
            assertEquals(0, interceptor.step);
            
            throw se;
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        fail("Security Exception expected when a resource is disabled");
    }
    
    @Test
    public void disabledBypass() throws Exception {
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.FALSE);
        context.method = CheckTrue.class.getDeclaredMethod("checkTrue", (Class[]) null);
        
        try {
            Object bypass = interceptor.grant(context);
            
            assertNull(bypass);
            assertEquals(0, context.proceedCount);
            assertEquals(0, interceptor.step);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void checkFalse() throws Exception {
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.TRUE);
        context.method = CheckFalse.class.getDeclaredMethod("checkFalse", (Class[]) null);
        
        try {
            interceptor.grant(context);
            
            assertEquals(1, context.proceedCount);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test(expected = SecurityException.class)
    public void disabledGrantCheckTrue() throws Exception {
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.TRUE);
        context.method = CheckTrue.class.getDeclaredMethod("checkTrue", (Class[]) null);
        
        try {
            interceptor.grant(context);
        } catch(SecurityException se) {
            assertEquals(0, context.proceedCount);
            
            throw se;
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        fail("Security Exception expected when a resource is disabled");
    }
    
    @Test(expected = SecurityException.class)
    public void unauthorised() throws Exception {
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.TRUE);
        context.method = CheckTrue.class.getDeclaredMethod("checkTrue", (Class[]) null);
        
        try {
            interceptor.grant(context);
        } catch (SecurityException se) {
            assertEquals("username", interceptor.authUser);
            assertEquals("CheckTrue", interceptor.authResource);
            assertEquals("checktrue", interceptor.authAction);
            assertEquals(0, context.proceedCount);
            
            throw se;
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void reflection() throws Exception {
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.TRUE);
        context.method = UnMarked.class.getDeclaredMethod("action", (Class[]) null);
        context.proceedResult = "Proceed";
        interceptor.authorised = true;
        
        try {
            Object result = interceptor.grant(context);
            
            assertEquals("username", interceptor.authUser);
            assertEquals(UnMarked.class.getName(), interceptor.authResource);
            assertEquals("action", interceptor.authAction);
            assertEquals(1, context.proceedCount);
            assertEquals("Proceed", result);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void noNameGrant() throws Exception {
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.TRUE);
        context.method = NoName.class.getDeclaredMethod("nothing", (Class[]) null);
        context.proceedResult = "Proceed";
        interceptor.authorised = true;
        
        try {
            Object result = interceptor.grant(context);
            
            assertEquals("username", interceptor.authUser);
            assertEquals(NoName.class.getName(), interceptor.authResource);
            assertEquals("nothing", interceptor.authAction);
            assertEquals(1, context.proceedCount);
            assertEquals("Proceed", result);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void authorisedNoFilter() throws Exception {
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.TRUE);
        context.method = CheckTrue.class.getDeclaredMethod("checkTrue", (Class[]) null);
        context.proceedResult = "Proceed";
        interceptor.authorised = true;
        
        try {
            Object result = interceptor.grant(context);
            
            assertEquals("username", interceptor.authUser);
            assertEquals("CheckTrue", interceptor.authResource);
            assertEquals("checktrue", interceptor.authAction);
            assertEquals(1, context.proceedCount);
            assertEquals("Proceed", result);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void resourceFilter() throws Exception {
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.TRUE);
        context.method = ResourceFilter.class.getMethod("filter", CheckTrue.class);
        interceptor.authorised = true;
        interceptor.result = "Result";
        
        try {
            Object result = interceptor.grant(context);
            
            assertEquals("username", interceptor.authUser);
            assertEquals("ResourceFilter", interceptor.authResource);
            assertEquals("filter", interceptor.authAction);
            assertEquals(context, interceptor.parameterContext);
            assertEquals("username", interceptor.parameterUser);
            assertEquals(context, interceptor.resultContext);
            assertEquals("username", interceptor.resultUser);
            assertEquals("Result", result);
            assertEquals(0, context.proceedCount);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void actionFilter() throws Exception {
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.TRUE);
        context.method = ActionFilter.class.getMethod("filter", CheckTrue.class);
        interceptor.authorised = true;
        interceptor.result = "Result";
        
        try {
            Object result = interceptor.grant(context);
            
            assertEquals("username", interceptor.authUser);
            assertEquals("ActionFilter", interceptor.authResource);
            assertEquals("action", interceptor.authAction);
            assertEquals(context, interceptor.parameterContext);
            assertEquals("username", interceptor.parameterUser);
            assertEquals(context, interceptor.resultContext);
            assertEquals("username", interceptor.resultUser);
            assertEquals("Result", result);
            assertEquals(0, context.proceedCount);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @After
    public void evaluate() {
        assertEquals(1, context.methodCount);
        
        switch (interceptor.step) {
            case 3: assertEquals(3, interceptor.resultStep);
            case 2: assertEquals(2, interceptor.parameterStep);
            case 1: assertEquals(1, interceptor.authoriseStep);
            case 0:
            break;
            default: fail();
        }
        
        switch (interceptor.step) {
            case 1: assertEquals(0, interceptor.parameterStep); //step 1
            case 2: assertEquals(0, interceptor.resultStep); //step 2
        }
    }
}
