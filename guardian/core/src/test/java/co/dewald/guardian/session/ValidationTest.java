package co.dewald.guardian.session;


import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import co.dewald.guardian.gate.Guardian;


/**
 * @see {@link GuardShift#validate(String)}
 * @see {@link GuardShift#authorise(String, String, String)}
 * @see {@link GuardShift#filter(String, String, Map)}
 * 
 * @author Dewald Pretorius
 */
public class ValidationTest {
    private static final String ERROR = "Illegal call on stack";
    private static final String USER = "username";
    private static final String RESOURCE = "resource";
    private static final String ACTION = "action";

    class DelegatedGuardian implements Guardian {

        int authCounter = 0;
        String authUsername;
        String authResource;
        String authAction;
        Boolean authorise;

        @Override
        public Boolean authorise(String username, String resource, String action) {
            authCounter++;
            authUsername = username;
            authResource = resource;
            authAction = action;
            return authorise;
        }

        int filterCounter = 0;
        String filterUsername;
        String filterResource;
        Map<String, ?> filterData;
        @SuppressWarnings("rawtypes")
        List filter;

        @SuppressWarnings("unchecked")
        @Override
        public <T> List<T> filter(String username, String resource, Map<String, T> data) {
            filterCounter++;
            filterUsername = username;
            filterResource = resource;
            filterData = data;
            return filter;
        }

        //@formatter:off
        @Override
        public Boolean authenticate(String username, String password) { throw new RuntimeException(ERROR); }
        @Override
        public Boolean checkState(String resource, String action) { throw new RuntimeException(ERROR); }
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

    @Before
    public void setUp() throws Exception {
        shift = new DecoratedShift();
        guardian = new DelegatedGuardian();
        shift.delegate = guardian;
        shift.session = new Session();
    }

    @Test(expected = SecurityException.class)
    public void validateUnauthenticated() {
        assertNull(shift.session.getLogin());
        
        try {
            shift.validate(USER);
        } catch (SecurityException se) {
            assertTrue(se.getMessage().contains(GuardShift.ERROR_UNAUTHENTICATED));
            assertTrue(se.getMessage().contains(USER));
            
            throw se;
        }
        
        fail("Expected a SecurityExceptionS");
    }
    
    @Test(expected = SecurityException.class)
    public void validateSessionUser() {
        shift.session.setUsername("session");
        shift.session.setLogin(new Date());
        
        try {
            shift.validate(USER);
        } catch (SecurityException se) {
            assertTrue(se.getMessage().contains(GuardShift.ERROR_MISMATCH));
            assertTrue(se.getMessage().contains(USER));
            assertEquals("session", shift.session.getUsername());
            
            throw se;
        }
        
        fail("Expected a SecurityExceptionS");
    }
    
    @Test
    public void passValidation() {
        shift.session.setLogin(new Date());
        shift.session.setUsername(USER);
        
        shift.validate(USER);
    }
    
    @Test
    public void authoriseValidation() {
        shift.session.setLogin(new Date());
        shift.session.setUsername(USER);
        guardian.authorise = Boolean.TRUE;
        
        Boolean authorise = shift.authorise(USER, RESOURCE, ACTION);
        
        assertEquals(1, guardian.authCounter);
        assertTrue(authorise);
        assertEquals(USER, guardian.authUsername);
        assertEquals(RESOURCE, guardian.authResource);
        assertEquals(ACTION, guardian.authAction);
    }
    
    @Test
    public void authoriseFilter() {
        shift.session.setLogin(new Date());
        shift.session.setUsername(USER);
        guardian.filter = Arrays.asList("Filtered", "Collection", "Elements");
        Map<String, String> data = new LinkedHashMap<>();
        data.put("1", "Filtered");
        data.put("2", "Collection");
        data.put("3", "Elements");
        
        List<String> filtered = shift.filter(USER, RESOURCE, data);
        
        assertEquals(1, guardian.filterCounter);
        assertEquals(guardian.filter, filtered);
        assertEquals(USER, guardian.filterUsername);
        assertEquals(RESOURCE, guardian.filterResource);
        assertEquals(data, guardian.filterData);
    }
}
