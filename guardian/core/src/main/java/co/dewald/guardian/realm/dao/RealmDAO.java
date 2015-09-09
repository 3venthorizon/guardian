package co.dewald.guardian.realm.dao;


import java.util.Collection;
import java.util.List;
import java.util.Set;

import co.dewald.guardian.realm.AccessLog;
import co.dewald.guardian.realm.Permission;
import co.dewald.guardian.realm.RealmEntity;
import co.dewald.guardian.realm.Role;
import co.dewald.guardian.realm.Subject;


/**
 * @author Dewald Pretorius
 */
public interface RealmDAO {

    /**
     * Creates an persists a new {@link RealmEntity}.
     * 
     * @param realmEntity
     */
    <RE extends RealmEntity> void create(RE realmEntity);

    void create(AccessLog event);

    /**
     * 
     * @param entityClass
     * @param primaryKeys
     * @return realmEntities list
     */
    <RE extends RealmEntity> List<RE> fetchAll(Class<RE> entityClass, Set<Long> primaryKeys);

    /**
     * Fetch permissions by resource.
     * 
     * @param resource
     * @return permission list
     */
    List<Permission> fetchPermissionsByResource(String resource);

    /**
     * Fetch roles by the supplied list of groups.
     * 
     * @param groups
     * @return role list
     */
    List<Role> fetchRolesBy(Collection<String> groups);

    /**
     * Fetch roles associated with the permission.
     * 
     * @param resource
     * @param action
     * @return role list
     */
    List<Role> fetchRolesByPermission(String resource, String action);

    /**
     * Fetch subjects by the supplied list of usernames.
     * 
     * @param usernames
     * @return subject list
     */
    List<Subject> fetchSubjectsBy(Collection<String> usernames);

    /**
     * Fetch subjects associated with the role.
     * 
     * @param group
     * @return subject list
     */
    List<Subject> fetchSubjectsByRole(String group);

    /**
     * Wrapper for the entity manager.
     * 
     * @param entityClass
     * @param primaryKey
     * @return entity
     */
    <RE extends RealmEntity> RE find(Class<RE> entityClass, Long primaryKey);

    /**
     * Finds the {@link Subject} by it's username.
     * 
     * @param username
     *            credentials
     * @param password
     *            credentials
     * @return subject
     */
    Subject findSubjectBy(String username);

    /**
     * Finds the {@link Role} by it's group
     * 
     * @param group
     * @return
     */
    Role findRoleBy(String group);

    /**
     * Finds the {@link Permission} on its unique keys: resource and action.
     * 
     * @param resource
     * @param action
     * @return permission or null when it is not found.
     */
    Permission findPermissionBy(String resource, String action);

    /**
     * Removes the realm entity.
     * 
     * @param realmEntity
     */
    <RE extends RealmEntity> void remove(RE realmEntity);

    /**
     * Updates the realm entity.
     * 
     * @param realmEntity
     * @return merged entity
     */
    <RE extends RealmEntity> RE update(RE realmEntity);

}
