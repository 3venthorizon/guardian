package co.dewald.guardian.gate;


import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;


/**
 * @author Dewald Pretorius
 */
@Grant(name = "Administration")
public interface Administration {
    
    /**
     * Register a new Subject.
     * 
     * @param username
     * @param password
     */
    void registerSubject(String username, String password);
    
    /**
     * Register a new Permission resource and action.
     * 
     * @param resource
     * @param action
     * @param active
     */
    void registerPermission(@NotNull String resource, @NotNull String action, boolean active);
    
    /**
     * Register a new Role that is in effect within a calendar timespan.
     * 
     * @param group
     */
    void registerRole(@NotNull String group);
    
    /**
     * Associates existing permissions with an existing role.
     * 
     * @param role name
     * @param resource 
     * @param actions
     */
    void mapRolePermissions(@NotNull String role, @NotNull String resource, @NotNull String action);
    
    /**
     * Associates existing roles with a subject.
     * 
     * @param username
     * @param roles
     */
    void mapSubjectRoles(@NotNull String username, @NotNull Set<String> roles);
    
    /**
     * Removes the Subject.
     * 
     * @param username
     */
    void removeSubject(@NotNull String username);
    
    /**
     * Removes the Role and detaches itself from associated Subjects.
     * 
     * @param role
     */
    void removeRole(@NotNull String role);
    
    /**
     * Removes the Permission and detaches itself from associated Roles.
     * 
     * @param resource
     * @param action
     */
    void removePermission(@NotNull String resource, @NotNull String action);
    
    /**
     * Remove Role Permission mapping.
     * 
     * @param role
     * @param resource
     * @param action
     */
    void removeRolePermission(@NotNull String role, @NotNull String resource, @NotNull String action);
    
    /**
     * Remove a Subject's Role mapping.
     * 
     * @param username
     * @param role
     */
    void removeSubjectRole(@NotNull String username, @NotNull String role);
    
    /**
     * Updates the Subject.
     * 
     * @param existing username
     * @param user
     * @param password
     */
    void updateSubject(String existing, String username, String password);
    
    /**
     * Activates or Deactivates Permission.
     * 
     * @param resource
     * @param action
     * @param active
     */
    void activatePermission(@NotNull String resource, @NotNull String action, boolean active);
    
    /**
     * View all Subjects.
     * 
     * @return usernames
     */
    List<String> viewSubjects();
    
    /**
     * View Roles by Subject.
     *  
     * @param username
     * @return roles by username
     */
    List<String> viewSubjectRoles(@NotNull String username);
    
    /**
     * View all Roles.
     * 
     * @return roles
     */
    List<String> viewRoles();
    
    /**
     * View Subjects by Role.
     * 
     * @param role
     * @return usernames
     */
    List<String> viewRoleSubjects(@NotNull String role);
    
    /**
     * View Permissions by Role.
     * 
     * @param role
     * @return permissions[resource, actions]
     */
    Map<String, Set<String>> viewRolePermissions(@NotNull String role);
    
    /**
     * View all Permissions.
     * 
     * @return permissions[resource, actions]
     */
    Map<String, Set<String>> viewPermissions();
    
    /**
     * View Roles by Permission.
     * 
     * @param resource
     * @param action
     * @return roles
     */
    List<String> viewPermissionRoles(@NotNull String resource, @NotNull String action);
}
