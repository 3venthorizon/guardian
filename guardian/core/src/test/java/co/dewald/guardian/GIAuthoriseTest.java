package co.dewald.guardian;


import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.dewald.guardian.gate.Guardian;


/**
 * @see GuardInterceptor#authorise(String, String, String)
 * @author Dewald Pretorius
 */
public class GIAuthoriseTest {
    private static final String ERROR = "Illegal call on stack";
    private static final String USER = "username";
    private static final String RESOURCE = "resource";
    private static final String ACTION = "action";
    
    class InjectedGuardian implements Guardian {
        Boolean authorise;
        String username;
        String resource;
        String action;
        int callCount = 0;
        
        @Override
        public Boolean authorise(String username, String resource, String action) {  
            callCount++;
            this.username = username;
            this.resource = resource;
            this.action = action;
            return authorise;
        }
        
        //formatter:off
        @Override
        public Boolean checkState(String resource, String action) { throw new RuntimeException(ERROR); }
        @Override 
        public Boolean authenticate(String username, String password) { throw new RuntimeException(ERROR); }
        @Override
        public <T> List<T> filter(String username, String resource, Map<String, T> data) { 
            throw new RuntimeException(ERROR); 
        }
        @Override
        public String getSessionToken(String username) { throw new RuntimeException(ERROR); }
        //formatter:on
    }
    
    GuardInterceptor interceptor;
    InjectedGuardian guardian;
    
    @Before
    public void prepare() {
        interceptor = new GuardInterceptor();
        guardian = new InjectedGuardian();
        interceptor.guardian = guardian;
    }
    
    @Test(expected = SecurityException.class)
    public void guardianReturnNull() {
        guardian.authorise = null;
        interceptor.authorise(USER, RESOURCE, ACTION);
    }
    
    @Test(expected = SecurityException.class)
    public void guardianReturnFalse() {
        guardian.authorise = Boolean.FALSE;
        interceptor.authorise(USER, RESOURCE, ACTION);
    }
    
    @Test
    public void guardianReturnTrue() {
        guardian.authorise = Boolean.TRUE;
        interceptor.authorise(USER, RESOURCE, ACTION);
    }
    
    @After
    public void evaluate() {
        assertEquals(USER, guardian.username);
        assertEquals(RESOURCE, guardian.resource);
        assertEquals(ACTION, guardian.action);
        assertEquals(1, guardian.callCount);
    }
}
