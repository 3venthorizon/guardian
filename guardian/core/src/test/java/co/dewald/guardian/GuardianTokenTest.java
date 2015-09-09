package co.dewald.guardian;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Dewald Pretorius
 */
public class GuardianTokenTest {
    
    GuardianEJB guardian;
    
    @Before
    public void setUp() {
        guardian = new GuardianEJB();
    }

    @Test
    public void getSessionToken() {
        String token = guardian.getSessionToken("username");
        
        assertNull(token);
    }
}
