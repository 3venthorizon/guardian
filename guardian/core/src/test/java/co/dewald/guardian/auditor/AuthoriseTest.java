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
public class AuthoriseTest {
    private static final String ERROR = "Illegal call on stack";
    private static final String USER = "username";
    private static final String RESOURCE = "resource";
    private static final String ACTION = "action";
    
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
        String resource;
        String action;
        Boolean authorise;
        boolean throwError = false;

        @Override
        public Boolean authorise(String username, String resource, String action) { 
            counter++;
            this.username = username;
            this.resource = resource;
            this.action = action;
            
            if (throwError) throw new RuntimeException("Testing");
            return authorise;
        }
        
        //@formatter:off
        @Override
        public Boolean authenticate(String username, String password) { throw new RuntimeException(ERROR); }
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

    @Before
    public void setUp() throws Exception {
        auditor = new DecoratedAuditor();
        realm = new Data();
        guardian = new DelegatedGuardian();
        auditor.delegate = guardian;
        auditor.realm = realm;
    }

    @Test
    public void failAuthorisation() {
        guardian.authorise = Boolean.FALSE;
        
        Boolean fail = auditor.authorise(USER, RESOURCE, ACTION);
        
        assertFalse(fail);
        assertFalse(realm.event.getGranted());
    }
    
    @Test
    public void noSubjectAuthorisation() {
        guardian.authorise = null;
        
        Boolean error = auditor.authorise(USER, RESOURCE, ACTION);
        
        assertNull(error);
        assertFalse(realm.event.getGranted());
        assertEquals(GuardAuditor.ERROR, realm.event.getMessage());
    }
    
    @Test(expected = Exception.class)
    public void errorAuthentication() {
        guardian.throwError = true;
        
        auditor.authorise(USER, RESOURCE, ACTION);
        
        assertFalse(realm.event.getGranted());
    }
    
    @Test
    public void passAuthorisation() {
        guardian.authorise = Boolean.TRUE;
        
        Boolean pass = auditor.authorise(USER, RESOURCE, ACTION);
        
        assertTrue(pass);
        assertTrue(realm.event.getGranted());
    }

    @After
    public void evaluate() {
        assertEquals(1, guardian.counter);
        assertNotNull(realm.event);
        assertEquals(USER, realm.event.getUsername());
        assertEquals(RESOURCE, guardian.resource);
        assertEquals(ACTION, guardian.action);
        assertNotNull(realm.event.getTimestamp());
        assertEquals(RESOURCE, realm.event.getResource());
        assertEquals(ACTION, realm.event.getAction());
    }
}
