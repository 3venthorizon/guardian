package co.dewald.guardian;


import co.dewald.guardian.gate.Administration;
import co.dewald.guardian.gate.Grant;
import co.dewald.guardian.gate.Guard;
import co.dewald.guardian.realm.Period;
import co.dewald.guardian.realm.Permission;
import co.dewald.guardian.realm.Role;
import co.dewald.guardian.realm.Subject;
import co.dewald.guardian.realm.dao.RealmDAO;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;


/**
 * AdministratorEJB
 */
@Guard
@Grant(name = "Administration")
@Stateless(name = "Bureaucrat")
public class AdministratorEJB implements Administration {
    
    private static final String RESOURCE = "Administration";
    
    @EJB RealmDAO realm;
    
    @PostConstruct
    void register() {
        Class<?> contract = Administration.class;
        Method[] methods = contract.getMethods();
        List<Permission> permissions = realm.fetchPermissionsByResource(RESOURCE);
        
        METHOD: for (Method method : methods) {
            String action = method.getName();
            
            for (Permission permission : permissions) {
                if (action.equals(permission.getAction())) continue METHOD;
            }
            
            Permission entity = new Permission(RESOURCE, action, true, false);
            realm.create(entity);
        }
    }

    /**
     * @see Administration#mapRolePermissions(String, String, Set<String>)
     */
    @Override
    public void mapRolePermissions(@NotNull String role, @NotNull String resource, @NotNull String action) {
        Role group = realm.findRoleBy(role);
        Permission permission = realm.findPermissionBy(resource, action);
        
        group.getPermissions().add(permission);
        realm.update(group);
    }

    /**
     * @see Administration#mapSubjectRoles(String, Set<String>)
     */
    @Override
    public void mapSubjectRoles(@NotNull String username, @NotNull Set<String> roles) {
        Subject subject = realm.findSubjectBy(username);
        List<Role> groups = realm.fetchRolesBy(roles);
        
        subject.getRoles().addAll(groups);
        realm.update(subject);
    }

    /**
     * @see Administration#registerPermission(String, String, boolean)
     */
    @Override
    public void registerPermission(@NotNull String resource, @NotNull String action, boolean active) {
        Permission permission = new Permission(resource, action, active, true);
        realm.create(permission);
    }

    /**
     * @see Administration#registerRole(String, Integer, Integer, Integer)
     */
    @Override
    public void registerRole(@NotNull String group, Integer calendarField, Integer start, Integer end) {
        Role role = new Role();
        Period period = new Period(calendarField, start, end);
        
        role.setGroup(group);
        role.setPeriod(period);
        
        realm.create(role);
    }

    /**
     * @see Administration#registerSubject(String, String)
     */
    @Override
    public void registerSubject(String username, String password) {
        Subject subject = new Subject();
        subject.setUsername(username);
        subject.setPassword(password);
        
        realm.create(subject);
    }

    /**
     * @see Administration#removePermission(String, String)
     */
    @Override
    public void removePermission(@NotNull String resource, @NotNull String action) {
        Permission permission = realm.findPermissionBy(resource, action);
        realm.remove(permission);
    }

    /**
     * @see Administration#removeRole(String)
     */
    @Override
    public void removeRole(@NotNull String role) {
        Role group = realm.findRoleBy(role);
        realm.remove(group);
    }

    /**
     * @see Administration#removeRolePermission(String, String, String)
     */
    @Override
    public void removeRolePermission(@NotNull String role, @NotNull String resource, @NotNull String action) {
        Role group = realm.findRoleBy(role);
        Permission permission = realm.findPermissionBy(resource, action);
        
        group.getPermissions().remove(permission);
        realm.update(group);
    }

    /**
     * @see Administration#removeSubject(String)
     */
    @Override
    public void removeSubject(@NotNull String username) {
        Subject subject = realm.findSubjectBy(username);
        realm.remove(subject);
    }

    /**
     * @see Administration#removeSubjectRole(String, String)
     */
    @Override
    public void removeSubjectRole(@NotNull String username, @NotNull String role) {
        Subject subject = realm.findSubjectBy(username);
        Role group = realm.findRoleBy(role);
        
        subject.getRoles().remove(group);
    }

    /**
     * @see Administration#activatePermission(String, String, boolean)
     */
    @Override
    public void activatePermission(@NotNull String resource, @NotNull String action, boolean active) {
        Permission permission = realm.findPermissionBy(resource, action);
        permission.setActive(active);
        
        realm.update(permission);
    }

    /**
     * @see Administration#updateSubject(String, String, String)
     */
    @Override
    public void updateSubject(String existing, String username, String password) {
        Subject subject = realm.findSubjectBy(existing);
        
        subject.setUsername(username);
        subject.setPassword(password);
        
        realm.update(subject);
    }

    /**
     * @see Administration#updateRole(String, String, Integer, Integer, Integer)
     */
    @Override
    public void updateRole(@NotNull String exsisting, @NotNull String role, 
                           Integer calendarField, Integer start, Integer end) {
        Role entity = realm.findRoleBy(exsisting);
        Period period = new Period(calendarField, start, end);
        
        entity.setGroup(role);
        entity.setPeriod(period);
        
        realm.update(entity);
    }

    /**
     * @see Administration#viewPermissions()
     */
    @Override
    public Map<String, Set<String>> viewPermissions() {
        List<Permission> permissions = realm.fetchAll(Permission.class, null);
        Map<String, Set<String>> list = new HashMap<String, Set<String>>(permissions.size());
        
        for (Permission permission : permissions) {
            Set<String> actions = list.get(permission.getResource());
            
            if (actions == null) {
                actions = new HashSet<>();
                list.put(permission.getResource(), actions);
            }
            
            actions.add(permission.getAction());
        }
        
        return list;
    }

    /**
     * @see Administration#viewPermissionRoles(String, String)
     */
    @Override
    public List<String> viewPermissionRoles(@NotNull String resource, @NotNull String action) {
        List<Role> roles = realm.fetchRolesByPermission(resource, action);
        List<String> list = new ArrayList<>(roles.size());
        
        for (Role role : roles) {
            list.add(role.getGroup());
        }
        
        return list;
    }

    /**
     * @see Administration#viewRoles()
     */
    @Override
    public List<String> viewRoles() {
        List<Role> roles = realm.fetchAll(Role.class, null);
        List<String> list = new ArrayList<>(roles.size());
        
        for (Role role : roles) {
            list.add(role.getGroup());
        }
        
        return list;
    }

    /**
     * @see Administration#viewRolePermissions(String)
     */
    @Override
    public Map<String, Set<String>> viewRolePermissions(@NotNull String group) {
        Role role = realm.findRoleBy(group);
        Set<Permission> permissions = role.getPermissions();
        Map<String, Set<String>> list = new HashMap<String, Set<String>>(permissions.size());
        
        for (Permission permission : permissions) {
            Set<String> actions = list.get(permission.getResource());
            
            if (actions == null) {
                actions = new HashSet<>();
                list.put(permission.getResource(), actions);
            }
            
            actions.add(permission.getAction());
        }
        
        return list;
    }

    /**
     * @see Administration#viewRoleSubjects(String)
     */
    @Override
    public List<String> viewRoleSubjects(@NotNull String role) {
        List<Subject> subjects = realm.fetchSubjectsByRole(role);
        List<String> list = new ArrayList<>(subjects.size());
        
        for (Subject subject : subjects) {
            list.add(subject.getUsername());
        }

        return list;
    }

    /**
     * @see Administration#viewSubjects()
     */
    @Override
    public List<String> viewSubjects() {
        List<Subject> subjects = realm.fetchAll(Subject.class, null);
        List<String> list = new ArrayList<>(subjects.size());
        
        for (Subject subject : subjects) {
            list.add(subject.getUsername());
        }
        
        return list;
    }

    /**
     * @see Administration#viewSubjectRoles(String)
     */
    @Override
    public List<String> viewSubjectRoles(@NotNull String username) {
        Subject subject = realm.findSubjectBy(username);
        Set<Role> roles = subject.getRoles();
        List<String> list = new ArrayList<>(roles.size());
        
        for (Role role : roles) {
            list.add(role.getGroup());
        }
        
        return list;
    }
}
