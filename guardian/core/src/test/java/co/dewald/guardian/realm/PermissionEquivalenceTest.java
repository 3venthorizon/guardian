package co.dewald.guardian.realm;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


/**
 * @see {@link Permission#equivalent(Permission)}
 * @see {@link Permission#equivalent(String, String)}
 * @author Dewald Pretorius
 */
public class PermissionEquivalenceTest {
    static final String RESOURCE = "resource";
    static final String ACTION = "action";
    
    Permission permission;
    
    @Before
    public void setUp() {
        permission = new Permission(RESOURCE, ACTION, Boolean.TRUE, Boolean.FALSE);
        permission.id = new Long(007L);
        
        assertTrue(permission instanceof RealmEntity);
    }
    
    @Test
    public void nullPermission() {
        Permission other = null;
        
        assertFalse(permission.equivalent(other));
    }
    
    @Test
    public void idsMatch() {
        Permission other = new Permission(RESOURCE, ACTION, Boolean.TRUE, Boolean.FALSE);
        other.id = new Long(007L);
        
        assertTrue(permission.equivalent(other));
    }
    
    @Test
    public void idsMisMatch() {
        Permission other = new Permission(RESOURCE, ACTION, Boolean.TRUE, Boolean.FALSE);
        other.id = new Long(006L);
        
        assertTrue(permission.equivalent(other));
    }
    
    @Test
    public void resourceMismatch() {
        Permission other = new Permission("Mismatch", ACTION, Boolean.TRUE, Boolean.FALSE);
        
        assertFalse(permission.equivalent(other));
        assertFalse(permission.equivalent("Mismatch", ACTION));
    }
    
    @Test
    public void thisActionALL() {
        Permission other = new Permission(RESOURCE, ACTION, Boolean.TRUE, Boolean.FALSE);
        permission.setAction(Permission.ALL);
        
        assertTrue(permission.equivalent(other));
        assertTrue(permission.equivalent(RESOURCE, ACTION));
    }
    
    @Test
    public void otherActionALL() {
        Permission other = new Permission(RESOURCE, Permission.ALL, Boolean.TRUE, Boolean.FALSE);
        
        assertTrue(permission.equivalent(other));
        assertTrue(permission.equivalent(RESOURCE, Permission.ALL));
    }
    
    @Test 
    public void actionMismatch() {
        Permission other = new Permission(RESOURCE, "Mismatch", Boolean.TRUE, Boolean.FALSE);
        
        assertFalse(permission.equivalent(other));
        assertFalse(permission.equivalent(RESOURCE, "Mismatch"));
    }
    
    @Test
    public void actionMatch() {
        Permission other = new Permission(RESOURCE, ACTION, Boolean.TRUE, Boolean.FALSE);
        
        assertTrue(permission.equivalent(other));
        assertTrue(permission.equivalent(RESOURCE, ACTION));
    }
}
