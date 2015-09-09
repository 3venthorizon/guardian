package co.dewald.guardian.session;


import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import javax.interceptor.InvocationContext;
import javax.validation.constraints.NotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.dewald.guardian.gate.Token;


/**
 * @author Dewald Pretorius
 */
public class GateKeeperTest {
    
    static final String ERROR = "Illegal call on stack";
    static final String PROCEED = "Return";
    static final String TOKEN = "TOKEN";
    
    //@formatter:off
    class NullParameters {
        public void action(byte primative, String string, Collection<?> collection, NullParameters resoursce) { }
    }
    
    class NoParameters {
        public void action(String none, @NotNull String notToken) { }
    }
    
    class TokenParameter {
        public void action(@NotNull String pointer, @NotNull @Token String token) { }
    }
    //@formatter:on
    
    class GateContext implements InvocationContext {
        Method method;
        Object[] parameters;
        Object proceed;
        int callMethod = 0;
        int callParameters = 0;
        int callProceed = 0;
        
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
        
        @Override
        public Object proceed() throws Exception {
            callProceed++;
            return proceed;
        }
        
        //formatter:off
        @Override
        public Object getTarget() { throw new RuntimeException(ERROR); }
        @Override
        public Object getTimer() { throw new RuntimeException(ERROR); }
        @Override
        public Constructor<?> getConstructor() { throw new RuntimeException(ERROR); }
        @Override
        public void setParameters(Object[] params) { throw new RuntimeException(ERROR); }
        @Override
        public Map<String, Object> getContextData() { throw new RuntimeException(ERROR); }
        //formatter:on
    }
    
    GateKeeper gateKeeper;
    GateContext context;
    Session tokenSession;

    @Before
    public void setUp() throws Exception {
        gateKeeper = new GateKeeper();
        tokenSession = new Session();
        tokenSession.setToken(TOKEN);
        gateKeeper.registry = new Registry(); //injected
        gateKeeper.registry.register(tokenSession);
        gateKeeper.session = new Session(); //injected
        context = new GateContext();
        context.proceed = PROCEED;
    }

    @Test
    public void noParameters() throws Exception {
        Object proceed = gateKeeper.open(context);
        
        assertNotEquals(tokenSession.getToken(), gateKeeper.session.getToken());
        assertNull(context.parameters);
        assertEquals(PROCEED, proceed);        
    }
    
    @Test
    public void emptyParameters() throws Exception {
        context.parameters = new Object[0];
        
        gateKeeper.open(context);
        
        assertNotEquals(tokenSession.getToken(), gateKeeper.session.getToken());
        assertEquals(0, context.parameters.length);
    }
    
    @Test
    public void nullParameters() throws Exception {
        context.method = 
            NullParameters.class.getMethod("action", byte.class, String.class, Collection.class, NullParameters.class); 
        int count = 4;
        context.parameters = new Object[count];

        gateKeeper.open(context);
        
        assertNotEquals(tokenSession.getToken(), gateKeeper.session.getToken());
        assertEquals(count, context.parameters.length);
        
        for (int x = 0; x < count; x++) {
            assertNull(context.parameters[x]);
        }
    }
    
    @Test
    public void noTokenAnnotations() throws Exception {
        context.method = NoParameters.class.getMethod("action", String.class, String.class);
        int count = 2;
        context.parameters = new Object[count];
        context.parameters[0] = null; //no annotation
        context.parameters[1] = "@javax.validation.constraints.NotNull";
        
        gateKeeper.open(context);
        
        assertNotEquals(tokenSession.getToken(), gateKeeper.session.getToken());
        assertNull(context.parameters[0]);
        assertEquals("@javax.validation.constraints.NotNull", context.parameters[1]);
    }
    
    @Test
    public void sessionNotFound() throws Exception {
        context.method = TokenParameter.class.getMethod("action", String.class, String.class);
        int count = 2;
        context.parameters = new Object[count];
        context.parameters[0] = "@javax.validation.constraints.NotNull"; 
        context.parameters[1] = "MyToken";
        
        gateKeeper.open(context);
        
        assertNotEquals(tokenSession.getToken(), gateKeeper.session.getToken());
        assertEquals("@javax.validation.constraints.NotNull", context.parameters[0]);
        assertEquals("MyToken", context.parameters[1]);
    }
    
    @Test
    public void sessionFound() throws Exception {
        context.method = TokenParameter.class.getMethod("action", String.class, String.class);
        int count = 2;
        context.parameters = new Object[count];
        context.parameters[0] = "@javax.validation.constraints.NotNull"; 
        context.parameters[1] = TOKEN;
        
        gateKeeper.open(context);
        
        assertEquals(tokenSession.getToken(), gateKeeper.session.getToken());
        assertFalse(tokenSession == gateKeeper.session);
        assertFalse(gateKeeper.registry.registry.values().contains(tokenSession));
        assertTrue(gateKeeper.registry.registry.values().contains(gateKeeper.session));
        assertEquals(TOKEN, gateKeeper.session.getToken());
        assertEquals(TOKEN, context.parameters[1]);
    }
    
    @After
    public void evaluate() {
        assertEquals(1, context.callProceed);
        assertEquals(1, context.callParameters);
        assertTrue(context.callMethod == 0 || context.callMethod == 1);
    }
}
