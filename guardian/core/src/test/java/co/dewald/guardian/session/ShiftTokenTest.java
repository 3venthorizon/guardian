package co.dewald.guardian.session;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Dewald Pretorius
 */
public class ShiftTokenTest {
    private static final String ERROR = "Illegal call on stack";
    private static final String USER = "username";
    
    class DecoratedShift extends GuardShift {
        //@formatter:off
        @Override
        public Boolean checkState(String resource, String action) { throw new RuntimeException(ERROR); }
        //@formatter:on
    }
    
    DecoratedShift shift;

    @Before
    public void setUp() throws Exception {
        shift = new DecoratedShift();
        shift.session = new Session();
    }

    @Test
    public void nullSessionToken() {
        assertNotEquals(shift.session.getUsername(), USER);
        
        String token = shift.getSessionToken(USER);
        
        assertNull(token);
    }
    
    @Test
    public void validSessionToken() {
        String token = shift.session.getToken();
        shift.session.setUsername(USER);
        
        String session = shift.getSessionToken(USER);
        
        assertEquals(token, session);
    }
}
