package co.dewald.guardian.realm.dao;


import static co.dewald.guardian.realm.Permission.*;
import static co.dewald.guardian.realm.Role.*;
import static co.dewald.guardian.realm.Subject.*;

import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import co.dewald.guardian.realm.AccessLog;
import co.dewald.guardian.realm.Permission;
import co.dewald.guardian.realm.RealmEntity;
import co.dewald.guardian.realm.Role;
import co.dewald.guardian.realm.Subject;


/**
 * Realm Data Access
 * 
 * @author Dewald Pretorius
 */
@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateless
public class Realm implements RealmDAO {
    
    @PersistenceContext(unitName = "realm") EntityManager em;
    
    @Override
    public <RE extends RealmEntity> void create(RE realmEntity) {
        em.persist(realmEntity);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void create(AccessLog event) {
        em.persist(event);
    }
    
    @Override
    public <RE extends RealmEntity> List<RE> fetchAll(Class<RE> entityClass, Set<Long> primaryKeys) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RE> cq = cb.createQuery(entityClass);
        Root<RE> entity = cq.from(entityClass);
        
        cq.select(entity);
        
        if (primaryKeys != null && !primaryKeys.isEmpty()) {
            cq.where(entity.in(primaryKeys));
        }
        
        TypedQuery<RE> query = em.createQuery(cq);
        List<RE> entities = query.getResultList();
        
        return entities;
    }
    
    @Override
    public <RE extends RealmEntity> RE find(Class<RE> entityClass, Long primaryKey) {
        return em.find(entityClass, primaryKey);
    }

    @Override
    public Subject findSubjectBy(String username) {
        TypedQuery<Subject> query = em.createNamedQuery(QUERY_SUBJECT, Subject.class);
        Subject subject = query.setParameter(PARAM_USERNAME, username)
                               .getSingleResult();
        return subject;
    }

    @Override
    public Role findRoleBy(String group) {
        TypedQuery<Role> query = em.createNamedQuery(QUERY_ROLE, Role.class);
        Role role = query.setParameter(PARAM_ROLE, group)
                         .getSingleResult();
        return role;
    }

    @Override
    public Permission findPermissionBy(String resource, String action) {
        TypedQuery<Permission> query = em.createNamedQuery(QUERY_PERMISSION, Permission.class);
        Permission permission = query.setParameter(PARAM_RESOURCE, resource)
                                     .setParameter(PARAM_ACTION, action)
                                     .getSingleResult();
        
        return permission;
    }
    
    //@formatter:off
    @Override
    public Permission loadPermission(String resource, String action) {
        TypedQuery<Permission> query = em.createNamedQuery(QUERY_PERMISSION, Permission.class);
        List<Permission> permission = query.setParameter(PARAM_RESOURCE, resource)
                                           .setParameter(PARAM_ACTION, action).getResultList();
        
        if (!permission.isEmpty()) return permission.get(0);
        
        Permission resourceAction = new Permission(resource, action, Boolean.TRUE, Boolean.FALSE);
        create(resourceAction);
        return resourceAction;
    }
    //@formatter:on

    @Override
    public <RE extends RealmEntity> void remove(RE realmEntity) {
        em.remove(realmEntity);
    }

    @Override
    public <RE extends RealmEntity> RE update(RE realmEntity) {
        return em.merge(realmEntity);
    }
    
    @Override
    public void linkUserRole(boolean link, String username, String roleGroup) {
        Subject subject = findSubjectBy(username);
        Role role = findRoleBy(roleGroup);
        Set<Role> roles = subject.getRoles();
        
        if (link) roles.add(role);
        else roles.remove(role);
        
        update(subject);
    }
    
    @Override
    public void linkRolePermission(boolean link, String roleGroup, String resource, String action) {
        Role role = findRoleBy(roleGroup);
        Permission permission = findPermissionBy(resource, action);
        Set<Permission> permissions = role.getPermissions();
        
        if (link) permissions.add(permission);
        else permissions.remove(permission);
        
        update(role);
    }
}
