package co.dewald.guardian.realm.dao;


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
     * @param username credentials
     * @param password credentials
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
     * Finds the Permission and return it. If no Permission is found create and return it.
     *  
     * @param resource
     * @param action
     * @return permission
     */
    Permission loadPermission(String resource, String action);
    
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

    /**
     * Manages the association between a {@link Subject} and it's {@link Role}s.
     *  
     * @param link associates a {@link Subject} to a {@link Role} when true. 
     * @param username
     * @param roleGroup
     */
    void linkUserRole(boolean link, String username, String roleGroup);

    /**
     * Manages the association between a {@link Role} and it's {@link Permission}s.
     * 
     * @param link
     * @param roleGroup
     * @param resource
     * @param action
     */
    void linkRolePermission(boolean link, String roleGroup, String resource, String action);

}
