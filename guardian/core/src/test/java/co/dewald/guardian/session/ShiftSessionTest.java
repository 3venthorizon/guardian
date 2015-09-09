package co.dewald.guardian.session;


import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import co.dewald.guardian.gate.Guardian;


/**
 * @author Dewald Pretorius
 */
public class ShiftSessionTest {
    private static final String ERROR = "Illegal call on stack";
    private static final String USER = "username";
    private static final String PASSWORD = "password";
    
    class DelegatedGuardian implements Guardian {
        int counter = 0;
        String username;
        String password;
        Boolean authenticate;

        @Override
        public Boolean authenticate(String username, String password) {
            counter++;
            this.username = username;
            this.password = password;
            return authenticate;
        }
        
        //@formatter:off
        @Override
        public Boolean authorise(String username, String resource, String action) { throw new RuntimeException(ERROR); }
        @Override
        public Boolean checkState(String resource, String action) { throw new RuntimeException(ERROR); }
        @Override
        public <T> List<T> filter(String username, String resource, Map<String, T> data) { 
            throw new RuntimeException(ERROR); 
        }
        @Override
        public String getSessionToken(String username) { throw new RuntimeException(ERROR); }
        //@formatter:on
    }
    
    //@formatter:off
    class DecoratedShift extends GuardShift {
        @Override
        public Boolean checkState(String resource, String action) { throw new RuntimeException(ERROR); }
    }
    //@formatter:on

    DecoratedShift shift;
    DelegatedGuardian guardian;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        shift = new DecoratedShift();
        guardian = new DelegatedGuardian();
        shift.delegate = guardian;
        shift.registry = new Registry();
        shift.session = new Session();
    }

    @Test
    public void failAuthentication() {
        assertTrue(shift.registry.registry.isEmpty());
        assertNotEquals(shift.session.getUsername(), USER);
        
        guardian.authenticate = Boolean.FALSE;
        
        Boolean authenticated = shift.authenticate(USER, PASSWORD);
        
        assertFalse(authenticated);
        assertEquals(1,  guardian.counter);
        assertEquals(USER, shift.session.getUsername());
        assertNull(shift.session.getLogin());
        assertNull(shift.registry.getSession(shift.session.getToken()));
        assertEquals(USER, guardian.username);
        assertEquals(PASSWORD, guardian.password);
    }

    @Test
    public void passAuthentication() {
        assertTrue(shift.registry.registry.isEmpty());
        assertNotEquals(shift.session.getUsername(), USER);
        
        guardian.authenticate = Boolean.TRUE;
        
        Boolean authenticated = shift.authenticate(USER, PASSWORD);
        
        assertTrue(authenticated);
        assertEquals(1,  guardian.counter);
        assertEquals(USER, shift.session.getUsername());
        assertNotNull(shift.session.getLogin());
        assertEquals(shift.session, shift.registry.getSession(shift.session.getToken()));
        assertEquals(USER, guardian.username);
        assertEquals(PASSWORD, guardian.password);
    }
}
