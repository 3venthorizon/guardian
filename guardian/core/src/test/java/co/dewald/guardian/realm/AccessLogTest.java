package co.dewald.guardian.realm;


import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Dewald Pretorius
 */
public class AccessLogTest {
    
    AccessLog accessLog;

    @Before
    public void setUp() throws Exception {
        accessLog = new AccessLog();
        accessLog.setMessage("Access Event Record");
    }

    @Test
    public void defaultConstructor() {
        assertNotNull(accessLog.getTimestamp());
        assertFalse(accessLog.getGranted());
    }

    @Test
    public void constructor() {
        Date now = new Date();
        accessLog = new AccessLog(now, "username", "resource", "action", true, "message");
        
        assertEquals(now, accessLog.getTimestamp());
        assertEquals("username", accessLog.getUsername());
        assertEquals("resource", accessLog.getResource());
        assertEquals("action", accessLog.getAction());
        assertTrue(accessLog.getGranted());
        assertEquals("message", accessLog.getMessage());
    }
    
    @Test
    public void setNullMessage() {
        assertNotNull(accessLog.getMessage());
        
        accessLog.setMessage(null);
        
        assertNull(accessLog.getMessage());
    }
    
    @Test
    public void truncatedMessage() {
        char[] characters = new char[AccessLog.MAX_TEXT];
        Arrays.fill(characters, '#');
        String hashTag = new String(characters);
        
        accessLog.setMessage(hashTag);
        
        assertEquals(100, accessLog.getMessage().length());
    }
}
