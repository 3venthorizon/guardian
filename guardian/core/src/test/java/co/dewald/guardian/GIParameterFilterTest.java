package co.dewald.guardian;


import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import javax.interceptor.InvocationContext;
import javax.validation.constraints.NotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.dewald.guardian.gate.Grant;


/**
 * @author Dewald Pretorius
 */
public class GIParameterFilterTest {
    static final String ERROR = "Illegal call on stack";
    static final String USER = "username";
    
    class PartialInterceptor extends GuardInterceptor {
        int callCount;
        String username;
        String filter;
        Object object;
        Object result;
        
        /**
         * Functionality not inherited.
         * 
         * @param username
         * @param resource
         * @param collection
         */
        @Override
        Object filter(String username, String filter, Object object) {
            callCount++;
            this.username = username;
            this.filter = filter;
            this.object = object;
            
            return result;
        }
    }
    
    //formatter:off
    @Grant(name = "Resource", filter = true)
    class NullParameters {
        public void action(@Grant(name = "Byte") byte primative, 
                           @Grant(filter = false) @NotNull String string, 
                           @Grant(filter = false) Set<Long> collection,
                           NullParameters resoursce) { }
    }
    
    @Grant(name = "Resource", filter = true)
    class NoParameters {
        public void action(String none, @NotNull String notGrant) { }
    }
    
    @Grant(name = "Resource", filter = true)
    class SkipParameters {
        public void action(@Grant(filter = false) String skip, String filter) {}
    }
    
    @Grant(name = "Resource")
    class NoNameParameters {
        public void action(@Grant(filter = true) String filter) {}
    }
    
    @Grant(name = "Resource", filter = true)
    class Comprehensive {
        public void action(String nullParam, 
                           Comprehensive filter,
                           @NotNull @Grant(filter = false) String skip,
                           @Grant(name = "rename") @NotNull String name) {}
    }
    //formatter:on
    
    class GuardContext implements InvocationContext {
        Method method;
        Object[] parameters;
        int callMethod = 0;
        int callParameters = 0;
        
        @Override
        public Object[] getParameters() {
            callParameters++;
            return parameters;
        }
        
        @Override
        public Method getMethod() {
            callMethod++;
            return method;
        }
        
        //formatter:off
        @Override
        public Object getTarget() { throw new RuntimeException(ERROR); }
        @Override
        public Object getTimer() { throw new RuntimeException(ERROR); }
        @Override
        public Constructor<?> getConstructor() { throw new RuntimeException(ERROR); }
        @Override
        public Object proceed() throws Exception { throw new RuntimeException(ERROR); }
        @Override
        public void setParameters(Object[] params) { throw new RuntimeException(ERROR); }
        @Override
        public Map<String, Object> getContextData() { throw new RuntimeException(ERROR); }
        //formatter:on
    }
    
    PartialInterceptor interceptor;
    GuardContext context;
    
    @Before
    public void prepare() {
        interceptor = new PartialInterceptor();
        context = new GuardContext();
    }
    
    @Test
    public void noParameters() {
        interceptor.filterParameters(context, USER);
        
        assertNull(context.parameters);
    }
    
    @Test
    public void emptyParameters() {
        context.parameters = new Object[0];
        
        interceptor.filterParameters(context, USER);
        
        assertEquals(0, context.parameters.length);
    }
    
    @Test
    public void nullParameters() throws Exception {
        context.method = 
                NullParameters.class.getMethod("action", byte.class, String.class, Set.class, NullParameters.class); 
        int count = 4;
        context.parameters = new Object[count];
        
        interceptor.filterParameters(context, USER);
        
        assertEquals(count, context.parameters.length);
        
        for (int x = 0; x < count; x++) {
            assertNull(context.parameters[x]);
        }
    }
    
    @Test
    public void noGrantAnnotations() throws Exception {
        context.method = NoParameters.class.getMethod("action", String.class, String.class);
        int count = 2;
        context.parameters = new Object[count];
        context.parameters[0] = null; //no annotation
        context.parameters[1] = "@javax.validation.constraints.NotNull";
        interceptor.result = "@NotNull";
               
        interceptor.filterParameters(context, USER);
        
        assertEquals(1, interceptor.callCount);
        assertEquals(USER, interceptor.username);
        assertNull(interceptor.filter);
        assertEquals("@javax.validation.constraints.NotNull", interceptor.object);
        assertEquals(count, context.parameters.length);
        assertNull(context.parameters[0]);
        assertEquals("@NotNull", context.parameters[1]);
    }
    
    @Test
    public void skipFilter() throws Exception {
        context.method = SkipParameters.class.getMethod("action", String.class, String.class);
        int count = 2;
        context.parameters = new Object[count];
        context.parameters[0] = "Skip";
        context.parameters[1] = "Filter";
        interceptor.result = "Java";
        
        interceptor.filterParameters(context, USER);
        
        assertEquals(1, interceptor.callCount);
        assertEquals(USER, interceptor.username);
        assertNull(interceptor.filter);
        assertEquals("Filter", interceptor.object);
        assertEquals(count, context.parameters.length);
        assertEquals("Skip", context.parameters[0]);
        assertEquals("Java", context.parameters[1]);
    }
    
    @Test
    public void noNameFilter() throws Exception {
        context.method = NoNameParameters.class.getMethod("action", String.class);
        int count = 1;
        context.parameters = new Object[count];
        context.parameters[0] = "NoName";
        interceptor.result = "Java";
        
        interceptor.filterParameters(context, USER);
        
        assertEquals(1, interceptor.callCount);
        assertEquals(USER, interceptor.username);
        assertNull(interceptor.filter);
        assertEquals("NoName", interceptor.object);
        assertEquals(count, context.parameters.length);
        assertEquals("Java", context.parameters[0]);
    }
    
    @Test
    public void comprehensive() throws Exception {
        context.method = 
                Comprehensive.class.getMethod("action", String.class, Comprehensive.class, String.class, String.class);
        int count = 4;
        context.parameters = new Object[count];
        context.parameters[0] = null;
        context.parameters[1] = new Comprehensive();
        context.parameters[2] = "Skip";
        context.parameters[3] = "Filter";
        interceptor.result = "Java";
        
        interceptor.filterParameters(context, USER);
        
        assertEquals(2, interceptor.callCount);
        assertEquals(USER, interceptor.username);
        assertEquals("rename", interceptor.filter);
        assertEquals("Filter", interceptor.object);
        assertEquals(count, context.parameters.length);
        assertNull(context.parameters[0]);
        assertEquals("Java", context.parameters[1]);
        assertEquals("Skip", context.parameters[2]);
        assertEquals("Java", context.parameters[3]);
    }
    
    @After
    public void evaluate() {
        assertEquals(1, context.callParameters);
        assertTrue(context.callMethod == 0 || context.callMethod == 1);
    }
}
