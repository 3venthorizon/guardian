package co.dewald.guardian.realm;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Dewald Pretorius
 */
public class SubjectTest {
    
    String salt = "yusEtXQXmmA28r1eAiFYxWGohUQxtImBR4SAipPQlngMxiTBr3n/kRk1qIlhg70f9ZzabxnBDwHDQutPCPu4JQ==";
    Subject subject;
    
    @Before
    public void setUp() {
        subject = new Subject();
        subject.setSalt();
        assertTrue(subject instanceof RealmEntity);
    }
    
    @Test
    public void getUsername() {
        subject.setUsername("username");
        
        assertEquals("username", subject.getUsername());
    }
    
    /**
     * Test method for {@link co.dewald.guardian.realm.Subject#hash(java.lang.String, java.lang.String)}.
     * 
     * @throws Exception
     */
    @Test
    public void hash() throws Exception {
        String hash = subject.hash("password", salt);

        assertEquals("gOnOrXc3oy8EVM/2Clg8eLQ6+qbdV+l3Iumz85WfkNla+GoZZQMRXD9dM8GFDVEz1dscq7ztzzDV9zDyE96w2g==", hash);
    }
    
    @Test
    public void mismatchBlankPassword() {
        boolean mismatch = subject.matchPassword("password");
        
        assertFalse(mismatch);
    }
    
    @Test
    public void mismatchPassword() {
        subject.setPassword("password");
        
        boolean mismatch = subject.matchPassword("wrong");
        
        assertFalse(mismatch); 
    }
    
    @Test 
    public void matchPassword() {
        subject.setPassword("password");
        
        boolean match = subject.matchPassword("password");

        assertTrue(match);
    }
    
    @Test(expected = RuntimeException.class)
    public void hashFailure() {
        @SuppressWarnings("serial")
        Subject subject = new Subject() {
            @Override
            String hash(String password, String salt) throws Exception {
                throw new RuntimeException(" Error Updating Password for Username: " + "username");
            }
        };
        
        subject.setPassword("password");
        fail("Hash failure is induced");
    }
}
