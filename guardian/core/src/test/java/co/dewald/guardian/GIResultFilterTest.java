package co.dewald.guardian;


import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import javax.interceptor.InvocationContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @see GuardInterceptor#filterResult(javax.interceptor.InvocationContext, String)
 * 
 * @author Dewald Pretorius
 */
public class GIResultFilterTest {
    static final String ERROR = "Illegal call on stack";
    static final String USER = "username";
    
    class PartialInterceptor extends GuardInterceptor {
        int callCount;
        String username;
        String filter;
        Object object;
        Object result;
        boolean clear;
        
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
    
    class GuardContext implements InvocationContext {
        int callCount;
        Object result;
        
        @Override
        public Object proceed() throws Exception {
            callCount++;
            return result;
        }

        //formatter:off
        @Override
        public Object getTarget() { throw new RuntimeException(ERROR); }
        @Override
        public Object getTimer() { throw new RuntimeException(ERROR); }
        @Override
        public Method getMethod() { throw new RuntimeException(ERROR); }
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
    
    PartialInterceptor interceptor;
    GuardContext context;
    
    @Before
    public void prepare() {
        interceptor = new PartialInterceptor();
        context = new GuardContext();
    }
    
    @Test
    public void nullResult() {
        try {
            context.result = null;
            
            Object result = interceptor.filterResult(context, USER);
            
            assertNull(result);
            assertEquals(0, interceptor.callCount);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void objectResult() {
        try {
            context.result = "Result object";
            interceptor.result = "Filtered result";
            
            Object result = interceptor.filterResult(context, USER);
            
            assertEquals(1, interceptor.callCount);
            assertEquals("Result object", interceptor.object);
            assertEquals("Filtered result", result);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @After
    public void evaluate() {
        assertEquals(1, context.callCount);
        assertNull(interceptor.filter);
    }
}
