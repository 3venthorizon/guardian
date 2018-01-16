package co.dewald.guardian;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.dewald.guardian.realm.Permission;
import co.dewald.guardian.realm.Role;
import co.dewald.guardian.realm.Subject;
import co.dewald.guardian.realm.dao.Realm;


/**
 * @author Dewald Pretorius
 */
public class GuardianAuthoriseTest {
    static final String USERNAME = "username";
    static final String RESOURCE = "resource";
    static final String ACTION = "action";
    
    class Data extends Realm {
        String resource;
        String action;
        Permission permission;
        String username;
        Subject subject;
        int callPermission = 0;
        int callSubject = 0;
        
        @Override
        public Permission findPermissionBy(String resource, String action) {
            callPermission++;
            this.resource = resource;
            this.action = action;
            return permission;
        }
        
        @Override
        public Subject findSubjectBy(String username) {
            callSubject++;
            this.username = username;
            return subject;
        }
    }
    
    GuardianEJB guardian;
    Data data;
    
    @Before
    public void setUp() {
        data = new Data();
        guardian = new GuardianEJB();
        guardian.realm = data;
    }
    
    @Test
    public void nullPermissionAndSubject() {
        Boolean authorised = guardian.authorise(USERNAME, RESOURCE, ACTION);
        
        assertNull(authorised);
    }
    
    @Test
    public void nullSubject() {
        data.permission = new Permission();
        
        Boolean authorised = guardian.authorise(USERNAME, RESOURCE, ACTION);
        
        assertTrue(!Boolean.TRUE.equals(authorised));
    }
    
    @Test
    public void nullPermission() {
        data.subject = new Subject();
        
        Boolean authorised = guardian.authorise(USERNAME, RESOURCE, ACTION);
        
        assertTrue(!Boolean.TRUE.equals(authorised));
    }
    
    @Test
    public void resourceDeactivated() {
        data.subject = new Subject();
        data.permission = new Permission();
        
        Boolean authorised = guardian.authorise(USERNAME, RESOURCE, ACTION);
        
        assertFalse(authorised);
    }
    
    @Test
    public void subjectEmptyRoles() {
        data.subject = new Subject();
        data.permission = new Permission();
        data.permission.setActive(Boolean.TRUE);
        
        Boolean authorised = guardian.authorise(USERNAME, RESOURCE, ACTION);
        
        assertFalse(authorised);
    }
    
    @Test
    public void roleEmptyPermissions() {
        data.subject = new Subject();
        Role role = new Role();
        role.setRole("Role");
        data.subject.getRoles().add(role);
        data.permission = new Permission();
        data.permission.setActive(Boolean.TRUE);
        
        Boolean authorised = guardian.authorise(USERNAME, RESOURCE, ACTION);
        
        assertFalse(authorised);
    }
    
    @Test
    public void permissionNotFound() {
        data.subject = new Subject();
        Permission mismatch = new Permission(ACTION, RESOURCE, Boolean.TRUE, Boolean.FALSE);
        Role role = new Role();
        role.setRole("Role");
        role.getPermissions().add(mismatch);
        data.subject.getRoles().add(role);
        data.permission = new Permission(RESOURCE, ACTION, Boolean.TRUE, Boolean.FALSE);
        
        Boolean authorised = guardian.authorise(USERNAME, RESOURCE, ACTION);
        
        assertFalse(authorised);
    }
    
    @Test
    public void permissionFound() {
        data.subject = new Subject();
        Permission found = new Permission(RESOURCE, ACTION, Boolean.TRUE, Boolean.FALSE);
        Role role = new Role();
        role.setRole("Role");
        role.getPermissions().add(found);
        data.subject.getRoles().add(role);
        data.permission = new Permission(RESOURCE, ACTION, Boolean.TRUE, Boolean.FALSE);
        
        Boolean authorised = guardian.authorise(USERNAME, RESOURCE, ACTION);
        
        assertTrue(authorised);
    }
    
    @After
    public void evaluate() {
        assertEquals(1, data.callPermission);
        assertEquals(1, data.callSubject);
        assertEquals(USERNAME, data.username);
        assertEquals(RESOURCE, data.resource);
        assertEquals(ACTION, data.action);
    }
}
