package co.dewald.guardian;


import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.dewald.guardian.realm.Period;
import co.dewald.guardian.realm.Permission;
import co.dewald.guardian.realm.Role;
import co.dewald.guardian.realm.Subject;
import co.dewald.guardian.realm.dao.Realm;


/**
 * @see {@link co.dewald.guardian.GuardianEJB#filter(java.lang.String, java.lang.String, java.util.Map)}
 * 
 * @author Dewald Pretorius
 */
public class GuardianFilterTest {
    static final String USERNAME = "username";
    static final String RESOURCE = "resource";
    static final String ACTIVE = "active";
    
    class Data extends Realm {
        String username;
        Subject subject;
        int callSubject = 0;
        
        @Override
        public Subject findSubjectBy(String username) {
            callSubject++;
            this.username = username;
            return subject;
        }
    }
    
    GuardianEJB guardian;
    Data data;
    Map<String, String> map;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        data = new Data();
        guardian = new GuardianEJB();
        guardian.realm = data;
        map = new LinkedHashMap<>();
        map.put(USERNAME, USERNAME);
        map.put(RESOURCE, RESOURCE);
        map.put(ACTIVE, ACTIVE);
    }

    @Test
    public void nullData() {
        List<?> nullPointer = guardian.filter(USERNAME, RESOURCE, null);
        
        assertNull(nullPointer);
        
        passEvaluate();
    }
    
    @Test
    public void emptyData() {
        Map<String, String> emptyData = new LinkedHashMap<>();
        
        List<String> list = guardian.filter(USERNAME, RESOURCE, emptyData);
        
        assertTrue(list.isEmpty());
        passEvaluate();
    }
    
    @Test
    public void faultTest() {
        List<String> list = guardian.filter(USERNAME, RESOURCE, map);
        
        assertNull(list);
    }
    
    @Test
    public void noRolesReturnEmpty() {
        data.subject = createSubject();
        
        List<String> list = guardian.filter(USERNAME, RESOURCE, map);
        
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void noPermissionsReturnEmpty() {
        data.subject = createSubject();
        addRole(data.subject, "role", null);
        addRole(data.subject, "group", null);
        
        List<String> list = guardian.filter(USERNAME, RESOURCE, map);
        
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void deactivePermissions() {
        data.subject = createSubject();
        Role role = addRole(data.subject, "role", null);
        Role group = addRole(data.subject, "group", null);
        Permission deactiveRole = new Permission(RESOURCE, "role", Boolean.FALSE, Boolean.FALSE);
        Permission deactiveGroup = new Permission(RESOURCE, "group", Boolean.FALSE, Boolean.FALSE);
        
        role.getPermissions().add(deactiveRole);
        role.getPermissions().add(deactiveGroup);
        group.getPermissions().add(deactiveGroup);
        group.getPermissions().add(deactiveRole);
        
        List<String> list = guardian.filter(USERNAME, RESOURCE, map);
        
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void allStarPermissions() {
        data.subject = createSubject();
        Role role = addRole(data.subject, "role", null);
        Role group = addRole(data.subject, "allStar", null);
        Permission permissionRole = new Permission(RESOURCE, "role", Boolean.TRUE, Boolean.FALSE);
        Permission permissionGroup = new Permission(RESOURCE, "*", Boolean.TRUE, Boolean.FALSE);
        
        role.getPermissions().add(permissionRole);
        group.getPermissions().add(permissionRole);
        group.getPermissions().add(permissionGroup);
        
        List<String> list = guardian.filter(USERNAME, RESOURCE, map);
        
        assertEquals(3, list.size());
        assertEquals(USERNAME, list.get(0));
        assertEquals(RESOURCE, list.get(1));
        assertEquals(ACTIVE, list.get(2));
    }
    
    @Test
    public void allStarActionResourceMismatch() {
        data.subject = createSubject();
        Role role = addRole(data.subject, "role", null);
        Role group = addRole(data.subject, "allStar", null);
        Permission permissionRole = new Permission(RESOURCE, "role", Boolean.TRUE, Boolean.FALSE);
        Permission permissionGroup = new Permission(RESOURCE, "*", Boolean.TRUE, Boolean.FALSE);
        
        role.getPermissions().add(permissionRole);
        group.getPermissions().add(permissionRole);
        group.getPermissions().add(permissionGroup);

        List<String> list = guardian.filter(USERNAME, "Mismatch", map);
        
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void retain1In3() {
        data.subject = createSubject();
        Role role = addRole(data.subject, "role", null);
        Role group = addRole(data.subject, "allStar", null);
        Permission permissionRole = new Permission(RESOURCE, "role", Boolean.TRUE, Boolean.FALSE);
        Permission permissionGroup = new Permission(RESOURCE, RESOURCE, Boolean.TRUE, Boolean.FALSE);
        
        role.getPermissions().add(permissionRole);
        group.getPermissions().add(permissionRole);
        group.getPermissions().add(permissionGroup);

        List<String> list = guardian.filter(USERNAME, RESOURCE, map);
        
        assertEquals(1, list.size());
        assertEquals(RESOURCE, list.get(0));
    }
    
    @Test 
    public void retainEachPermission() {
        data.subject = createSubject();
        Role role = addRole(data.subject, "role", null);
        Role group = addRole(data.subject, "allStar", null);
        Permission permissionRole = new Permission(RESOURCE, USERNAME, Boolean.TRUE, Boolean.FALSE);
        Permission permissionGroup = new Permission(RESOURCE, RESOURCE, Boolean.TRUE, Boolean.FALSE);
        Permission permissionActive = new Permission(RESOURCE, ACTIVE, Boolean.TRUE, Boolean.FALSE);
        
        role.getPermissions().add(permissionRole);
        role.getPermissions().add(permissionActive);
        group.getPermissions().add(permissionRole);
        group.getPermissions().add(permissionGroup);
        group.getPermissions().add(permissionActive);

        List<String> list = guardian.filter(USERNAME, RESOURCE, map);
        
        assertEquals(3, list.size());
        assertEquals(USERNAME, list.get(0));
        assertEquals(RESOURCE, list.get(1));
        assertEquals(ACTIVE, list.get(2));
    }
    
    @Test
    public void outsideRolePeriod() {
        data.subject = createSubject();
        Period outside = new Period(Calendar.YEAR, null, 2000);
        Role role = addRole(data.subject, "role", outside);
        Role group = addRole(data.subject, "allStar", outside);
        Permission permissionRole = new Permission(RESOURCE, USERNAME, Boolean.TRUE, Boolean.FALSE);
        Permission permissionGroup = new Permission(RESOURCE, RESOURCE, Boolean.TRUE, Boolean.FALSE);
        Permission permissionActive = new Permission(RESOURCE, ACTIVE, Boolean.TRUE, Boolean.FALSE);
        
        role.getPermissions().add(permissionRole);
        role.getPermissions().add(permissionActive);
        group.getPermissions().add(permissionRole);
        group.getPermissions().add(permissionGroup);
        group.getPermissions().add(permissionActive);

        List<String> list = guardian.filter(USERNAME, RESOURCE, map);
        
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void insideRolePeriod() {
        data.subject = createSubject();
        Period inside = new Period(Calendar.YEAR, 2000, null);
        Role role = addRole(data.subject, "role", inside);
        Role group = addRole(data.subject, "allStar", inside);
        Permission permissionRole = new Permission(RESOURCE, USERNAME, Boolean.TRUE, Boolean.FALSE);
        Permission permissionGroup = new Permission(RESOURCE, RESOURCE, Boolean.TRUE, Boolean.FALSE);
        Permission permissionActive = new Permission(RESOURCE, ACTIVE, Boolean.TRUE, Boolean.FALSE);
        
        role.getPermissions().add(permissionRole);
        role.getPermissions().add(permissionActive);
        group.getPermissions().add(permissionRole);
        group.getPermissions().add(permissionGroup);
        group.getPermissions().add(permissionActive);

        List<String> list = guardian.filter(USERNAME, RESOURCE, map);
        
        assertEquals(3, list.size());
        assertEquals(USERNAME, list.get(0));
        assertEquals(RESOURCE, list.get(1));
        assertEquals(ACTIVE, list.get(2));
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void evaluate() throws Exception {
        assertEquals(1, data.callSubject);
        assertEquals(USERNAME, data.username);
    }
    
    private Subject createSubject() {
        Subject subject = new Subject();
        subject.setUsername(USERNAME);
        
        return subject;
    }
    
    private Role addRole(Subject subject, String group, Period period) {
        Role role = new Role(group, period);
        subject.getRoles().add(role);
        
        return role;
    }
    
    private void passEvaluate() {
        data.username = USERNAME;
        data.callSubject = 1;
    }
}
