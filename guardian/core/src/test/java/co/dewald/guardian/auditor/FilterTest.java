package co.dewald.guardian.auditor;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
public class FilterTest {
    private static final String ERROR = "Illegal call on stack";
    private static final String USER = "username";
    private static final String RESOURCE = "resource";
    
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
        Map<String, ?> data;
        @SuppressWarnings("rawtypes")
        List filter;
        boolean throwError = false;

        @SuppressWarnings("unchecked")
        @Override
        public <T> List<T> filter(String username, String resource, Map<String, T> data) { 
            counter++;
            this.username = username;
            this.resource = resource;
            this.data = data;
            
            if (throwError) throw new RuntimeException("Testing");
            return filter;
        }
        
        //@formatter:off
        @Override
        public Boolean authenticate(String username, String password) { throw new RuntimeException(ERROR); }
        @Override
        public Boolean authorise(String username, String resource, String action) { throw new RuntimeException(ERROR); }
        @Override
        public Boolean checkState(String resource, String action) { throw new RuntimeException(ERROR); }
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
    public void nullData() {
        guardian.filter = new ArrayList<String>();
        
        List<String> filtered = auditor.filter(USER, RESOURCE, null);
        
        assertEquals(guardian.filter, filtered);
        assertNull(guardian.data);
        assertEquals(GuardAuditor.ERROR_NULL_FILTER, realm.event.getMessage());
        assertFalse(realm.event.getGranted());
    }
    
    @Test
    public void noSubjectFilter() {
        Map<String, String> data = new LinkedHashMap<>();
        
        List<String> filtered = auditor.filter(USER, RESOURCE, data);
        
        assertNull(filtered);
        assertEquals(guardian.data, data);
        assertEquals(GuardAuditor.ERROR, realm.event.getMessage());
        assertFalse(realm.event.getGranted());
    }
    
    @Test
    public void filteredSubSet() {
        guardian.filter = Arrays.asList("one", "three");
        Map<String, String> data = new LinkedHashMap<>();
        data.put("1", "one");
        data.put("2", "two");
        data.put("3", "three");
        
        List<String> filtered = auditor.filter(USER, RESOURCE, data);
        
        assertEquals(guardian.filter,  filtered);
        assertEquals(guardian.data, data);
        assertTrue(realm.event.getMessage().contains(GuardAuditor.ERROR_FILTERED));
        assertFalse(realm.event.getGranted());
    }
    
    @Test(expected = Exception.class)
    public void errorFilter() {
        guardian.throwError = true;
        Map<String, String> data = new LinkedHashMap<>();
        data.put("1", "one");
        data.put("2", "two");
        data.put("3", "three");
        
        auditor.filter(USER, RESOURCE, data);
        
        assertEquals(guardian.data, data);
        assertFalse(realm.event.getGranted());
    }
    
    @Test
    public void passFilter() {
        guardian.filter = Arrays.asList("one", "two", "three");
        Map<String, String> data = new LinkedHashMap<>();
        data.put("1", "one");
        data.put("2", "two");
        data.put("3", "three");
        
        List<String> filtered = auditor.filter(USER, RESOURCE, data);
        
        assertEquals(guardian.filter,  filtered);
        assertEquals(guardian.data, data);
        assertTrue(realm.event.getGranted());
    }

    @After
    public void evaluate() {
        assertEquals(1, guardian.counter);
        assertNotNull(realm.event);
        assertEquals(USER, realm.event.getUsername());
        assertEquals(RESOURCE, guardian.resource);
        assertNotNull(realm.event.getTimestamp());
        assertEquals(RESOURCE, realm.event.getResource());
        assertEquals(GuardAuditor.ACTION_FILTER, realm.event.getAction());
    }
}
