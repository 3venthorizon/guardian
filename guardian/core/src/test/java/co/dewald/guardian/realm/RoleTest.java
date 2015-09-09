/**
 * 
 */
package co.dewald.guardian.realm;


import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Dewald Pretorius
 */
public class RoleTest {

    Role role;
    
    /**
     * Test method for {@link co.dewald.guardian.realm.Role#setPeriod(co.dewald.guardian.realm.Period)}.
     */
    @Before
    public void setUp() {
        Period period = new Period(Calendar.YEAR, 2000, null);
        role = new Role("group", period);
        
        assertTrue(role instanceof RealmEntity);
        assertEquals("group", role.getGroup());
    }

    @Test
    public void setNullPeriod() {
        assertNotNull(role.getPeriod());
        
        role.setPeriod(null);
        
        assertNull(role.getPeriod());
    }
    
    @Test 
    public void setInvalidPeriod() {
        assertNotNull(role.getPeriod());
        Period invalid = new Period(null, 2000, 3000);
        
        role.setPeriod(invalid);
        
        assertNull(role.getPeriod());
    }
    
    @Test
    public void setValidPeriod() {
        Period valid = new Period(Calendar.YEAR, null, 2000);
        assertNotNull(role.getPeriod());
        assertNotEquals(role.getPeriod(), valid);
        
        role.setPeriod(valid);
        
        assertEquals(valid, role.getPeriod());
    }
}
