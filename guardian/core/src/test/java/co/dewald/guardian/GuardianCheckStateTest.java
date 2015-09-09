package co.dewald.guardian;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import co.dewald.guardian.realm.Permission;
import co.dewald.guardian.realm.dao.RealmDAO;


@RunWith(MockitoJUnitRunner.class)
public class GuardianCheckStateTest {
    
    static final String RESOURCE = "resource";
    static final String ACTION = "action";
    
    @Mock RealmDAO realm;
    GuardianEJB guardian;

    @Before
    public void setUp() throws Exception {
        guardian = new GuardianEJB();
        guardian.realm = realm;
    }

    @Test(expected = SecurityException.class)
    public void permissionNotFound() {
        when(realm.findPermissionBy(RESOURCE, ACTION)).thenThrow(new RuntimeException("Permission not found"));
        
        guardian.checkState(RESOURCE, ACTION);
    }

    @Test
    public void active() {
        Permission permission = new Permission(RESOURCE, ACTION, Boolean.TRUE, Boolean.FALSE);
        when(realm.findPermissionBy(RESOURCE, ACTION)).thenReturn(permission);
        
        Boolean state = guardian.checkState(RESOURCE, ACTION);
        
        assertTrue(state);
    }
    
    @Test
    public void bypassDeactive() {
        Permission permission = new Permission(RESOURCE, ACTION, Boolean.FALSE, Boolean.TRUE);
        when(realm.findPermissionBy(RESOURCE, ACTION)).thenReturn(permission);
        
        Boolean state = guardian.checkState(RESOURCE, ACTION);
        
        assertFalse(state);
    }
    
    @Test(expected = SecurityException.class)
    public void deactive() {
        Permission permission = new Permission(RESOURCE, ACTION, Boolean.FALSE, Boolean.FALSE);
        when(realm.findPermissionBy(RESOURCE, ACTION)).thenReturn(permission);
        
        guardian.checkState(RESOURCE, ACTION);
    }
}
