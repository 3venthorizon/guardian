package co.dewald.guardian;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.dewald.guardian.realm.Subject;
import co.dewald.guardian.realm.dao.Realm;


/**
 * @author Dewald Pretorius
 */
public class GuardianAuthenticateTest {
    static final String PASSWORD = "P455w0rd";
    static final String USERNAME = "username";
    
    class Data extends Realm {
        String username;
        Subject result;
        int callCount = 0;
        
        @Override
        public Subject findSubjectBy(String username) {
            callCount++;
            this.username = username;
            return result;
        }
    }
    
    GuardianEJB guardian;
    Data data;
    
    @Before
    public void setUp() {
        data = new Data();
        guardian = new GuardianEJB();
        guardian.realm = data;
    }
    
    @Test 
    public void subjectNotFound() {
        data.result = null;
        
        Boolean authenticated = guardian.authenticate(USERNAME, PASSWORD);
        
        assertNull(authenticated);
    }
    
    @Test
    public void wrongPassword() {
        data.result = new Subject();
        data.result.setUsername(USERNAME);
        data.result.setPassword(PASSWORD);

        Boolean authenticated = guardian.authenticate(USERNAME, "Wr0ngPa55");
        
        assertFalse(authenticated);
    }
    
    @Test
    public void matchPassword() {
        data.result = new Subject();
        data.result.setUsername(USERNAME);
        data.result.setPassword(PASSWORD);

        Boolean authenticated = guardian.authenticate(USERNAME, PASSWORD);
        
        assertTrue(authenticated);
    }
    
    @After
    public void evaluate() {
        assertEquals(USERNAME, data.username);
        assertEquals(1, data.callCount);
    }
}
