package co.dewald.guardian.realm.dao;


import java.util.List;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import co.dewald.guardian.admin.dao.PermissionDAO;
import co.dewald.guardian.dto.User;
import co.dewald.guardian.gate.Guard;
import co.dewald.guardian.realm.Permission;
import co.dewald.guardian.realm.Role;
import co.dewald.guardian.realm.Subject;


/**
 * @author Dewald Pretorius
 */
//FIXME @Guard
@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateless(name = "PermissionDAO")
public class PermissionEJB implements Model2DTO<Permission, co.dewald.guardian.dto.Permission>, PermissionDAO {

    @PersistenceContext(unitName = "realm") EntityManager em;
    @EJB RealmDAO realm;
    
    static final Function<Permission, co.dewald.guardian.dto.Permission> MODEL2DTO = permission -> {
        co.dewald.guardian.dto.Permission dto = new co.dewald.guardian.dto.Permission();
        dto.setResource(permission.getResource());
        dto.setAction(permission.getAction());
        dto.setActive(permission.getActive());
        dto.setBypass(permission.getBypass());
        
        return dto;
    };
    
    static final Function<co.dewald.guardian.dto.Permission, Permission> DTO2MODEL = dto -> {
        Permission permission = new Permission();
        permission.setResource(dto.getResource());
        permission.setAction(dto.getAction());
        permission.setActive(dto.isActive());
        permission.setBypass(dto.isBypass());
        
        return permission;
    };

    @Override
    public Function<Permission, co.dewald.guardian.dto.Permission> model2dto() {
        return MODEL2DTO;
    }

    @Override
    public List<co.dewald.guardian.dto.Permission> fetch() {
        TypedQuery<Permission> query = em.createNamedQuery(Permission.QUERY_ALL, Permission.class);
        return fetch(query);
    }

    @Override
    public List<co.dewald.guardian.dto.Permission> fetchBy(co.dewald.guardian.dto.Role role) {
        TypedQuery<Permission> query = em.createNamedQuery(Permission.QUERY_BY_ROLE, Permission.class);
        query.setParameter(Role.PARAM_ROLE, role.getGroup());
        
        return fetch(query);
    }

    @Override
    public List<co.dewald.guardian.dto.Permission> fetchBy(User user) {
        TypedQuery<Permission> query = em.createNamedQuery(Permission.QUERY_BY_SUBJECT, Permission.class);
        query.setParameter(Subject.PARAM_USERNAME, user.getUsername());
        
        return fetch(query);
    }

    @Override
    public co.dewald.guardian.dto.Permission find(co.dewald.guardian.dto.Permission uniqueKey) {
        Permission permission = realm.findPermissionBy(uniqueKey.getResource(), uniqueKey.getAction());
        return MODEL2DTO.apply(permission);
    }

    @Override
    public void create(co.dewald.guardian.dto.Permission dto) {
        realm.create(DTO2MODEL.apply(dto));
    }

    @Override
    public void update(co.dewald.guardian.dto.Permission id, co.dewald.guardian.dto.Permission dto) {
        Permission permission = realm.findPermissionBy(id.getResource(), id.getAction());
        permission.setResource(dto.getResource());
        permission.setAction(dto.getAction());
        permission.setActive(dto.isActive());
        permission.setBypass(dto.isBypass());
        
        realm.update(permission);
    }

    @Override
    public void delete(co.dewald.guardian.dto.Permission id) {
        Permission permission = realm.findPermissionBy(id.getResource(), id.getAction());
        realm.remove(permission);
    }

    @Override
    public void link(boolean link, co.dewald.guardian.dto.Permission permission, co.dewald.guardian.dto.Role role) {
        realm.linkRolePermission(link, role.getGroup(), permission.getResource(), permission.getAction());
    }
}
