package co.dewald.guardian.session;


import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Dewald Pretorius
 */
public class SessionTest {
    
    Session session;
    Date date;
    Date pointer;
    
    @Before
    public void setUp() {
        session = new Session();
        pointer = session.getTouched();
        date = new Date(session.getTouched().getTime());
    }

    /**
     * Test method for {@link co.dewald.guardian.Session#Session()}.
     */
    @Test
    public void constructor() {
        assertNotNull(session.getTouched());
        assertNotNull(session.getToken());
        assertNotNull(session.getUsername());
    }

    @Test
    public void touched() {
        Method[] methods = Session.class.getDeclaredMethods();
        
        for (Method method : methods) {
            if (!method.getName().startsWith("get") && !method.getName().startsWith("set")) continue;
            if ("getTouched".equals(method.getName())) continue;
            
            Class<?>[] types = method.getParameterTypes();
            Object[] args = new Object[types.length];
            
            try {
                Thread.sleep(1);
                method.invoke(session, args);
            } catch (Exception e) {
            }
            
            assertTrue(date.before(session.getTouched()));
        }
    }
    
    @Test
    public void logOut() {
        session.setLogin(new Date());
        
        assertNotNull(session.getLogin());
        
        session.setLogin(null); //logout
        
        assertNull(session.getLogin());
    }
    
    @Test
    public void newLogin() {
        Date now = new Date();
        assertNull(session.getLogin());
        
        session.setLogin(now);
        
        assertEquals(now, session.getLogin());
        assertFalse(now == session.getLogin()); //not the same object
    }
    
    @Test
    public void relogin() {
        session.setLogin(new Date());
        Date loggedIn = session.getLogin();
        
        assertNotNull(loggedIn);
        
        Date now = new Date();
        
        session.setLogin(now);
        
        assertEquals(now, session.getLogin());
        assertFalse(now == session.getLogin()); //not the same object
        assertTrue(loggedIn == session.getLogin()); //original pointer
    }
    
    @Test
    public void merge() {
        Session that = new Session();
        that.setUsername("password");
        that.setLogin(new Date());

        String username = that.getUsername();
        String token = that.getToken();
        Date login = that.getLogin();
        Date touched = that.getTouched();
        
        session.merge(that);
        
        assertTrue(username == session.username);
        assertTrue(token == session.token);
        assertTrue(login == session.login);
        assertTrue(touched == session.touched);
        
        pointer = touched;
    }
    
    @Test
    public void evaluate() {
        assertEquals(pointer, session.getTouched());
    }
}
