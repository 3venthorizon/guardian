package co.dewald.guardian.auditor;


import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.dewald.guardian.gate.Guardian;
import co.dewald.guardian.realm.AccessLog;
import co.dewald.guardian.realm.dao.Realm;


/**
 * @author Dewald Pretorius
 */
public class AuthenticateTest {
    private static final String ERROR = "Illegal call on stack";
    private static final String USER = "username";
    private static final String PASSWORD = "password";
    
    class Data extends Realm {
        int counter = 0;
        AccessLog event;
        
        @Override
        public void create(AccessLog event) {
            counter++;
            this.event = event;
        }
    }
    
    class DelegatedGuardian implements Guardian {
        int counter = 0;
        String username;
        String password;
        Boolean authenticate;
        boolean throwError = false;

        @Override
        public Boolean authenticate(String username, String password) {
            counter++;
            this.username = username;
            this.password = password;
            
            if (throwError) throw new RuntimeException("Testing");
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
    class DecoratedAuditor extends GuardAuditor {
        @Override
        public Boolean checkState(String resource, String action) { throw new RuntimeException(ERROR); }
        @Override
        public String getSessionToken(String username) { throw new RuntimeException(ERROR); }
    }
    //@formatter:on
    
    DecoratedAuditor auditor;
    Data realm;
    DelegatedGuardian guardian;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        auditor = new DecoratedAuditor();
        realm = new Data();
        guardian = new DelegatedGuardian();
        auditor.delegate = guardian;
        auditor.realm = realm;
    }

    @Test
    public void failAuthentication() {
        guardian.authenticate = Boolean.FALSE;
        
        Boolean fail = auditor.authenticate(USER, PASSWORD);
        
        assertFalse(fail);
    }
    
    @Test
    public void noSubjectAuthentication() {
        guardian.authenticate = null;
        
        Boolean error = auditor.authenticate(USER, PASSWORD);
        
        assertNull(error);
        assertFalse(realm.event.getGranted());
        assertEquals(GuardAuditor.ERROR_NOSUBJECT, realm.event.getMessage());
    }
    
    @Test(expected = Exception.class)
    public void errorAuthentication() {
        guardian.throwError = true;
        
        auditor.authenticate(USER, PASSWORD);
        
        assertFalse(realm.event.getGranted());
    }
    
    @Test
    public void passAuthentication() {
        guardian.authenticate = Boolean.TRUE;
        
        Boolean pass = auditor.authenticate(USER, PASSWORD);
        
        assertTrue(pass);
        assertTrue(realm.event.getGranted());
    }

    @After
    public void evaluate() {
        assertEquals(1, guardian.counter);
        assertNotNull(realm.event);
        assertEquals(USER, guardian.username);
        assertEquals(PASSWORD, guardian.password);
        assertEquals(GuardAuditor.RESOURCE, realm.event.getResource());
        assertEquals(GuardAuditor.ACTION_AUTHENTICATE, realm.event.getAction());
        assertEquals(USER, realm.event.getUsername());
        assertNotNull(realm.event.getTimestamp());
    }
}
