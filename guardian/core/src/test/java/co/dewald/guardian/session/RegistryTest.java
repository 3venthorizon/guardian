package co.dewald.guardian.session;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Dewald Pretorius
 */
public class RegistryTest {
    
    static final String USER = "username"; 

    Registry registry;
    Session session;

    @Before
    public void setUp() throws Exception {
        registry = new Registry();
        session = new Session();
        
        assertNotNull(registry.registry);
    }

    @Test
    public void register() {
        assertEquals(0, registry.registry.size());
        
        registry.register(session);
        
        assertEquals(1, registry.registry.size());
        assertTrue(registry.registry.values().contains(session));
    }

    @Test
    public void getSession() {
        String token = session.getToken();
        registry.register(session);
        
        Session session = registry.getSession(token);
        
        assertNotNull(session);
        assertEquals(this.session, session);
    }

    @Test
    public void expire() {
        String token = session.getToken();
        session.setUsername(USER);
        registry.register(session);
        registry.expire(token);
        
        Session session = registry.getSession(token);
        
        assertNull(session);
    }
}
