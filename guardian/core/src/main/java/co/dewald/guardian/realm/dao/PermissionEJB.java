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

import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.DTO;
import co.dewald.guardian.dto.User;
import co.dewald.guardian.gate.Grant;
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
public class PermissionEJB implements Model2DTO<Permission, co.dewald.guardian.dto.Permission>, 
                                      DAO<co.dewald.guardian.dto.Permission> {

    @PersistenceContext(unitName = "realm") EntityManager em;
    @EJB RealmDAO realm;
    
    static final Function<Permission, co.dewald.guardian.dto.Permission> MODEL2DTO = permission -> {
        if (permission == null) return null;
        
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
    public String getId(co.dewald.guardian.dto.Permission id) {
        return id.getResource() + ':' + id.getAction();
    }
    
    @Override
    public co.dewald.guardian.dto.Permission getId(String id) {
        String[] splitIds = id.split(":");
        co.dewald.guardian.dto.Permission permissionId = new co.dewald.guardian.dto.Permission();
        permissionId.setResource(splitIds[0]);
        permissionId.setAction(splitIds[1]);
        
        return permissionId;
    }

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
    public <C extends DTO> List<co.dewald.guardian.dto.Permission> fetchBy(C criteria) {
        try {
            TypedQuery<Permission> query;
            
            if (criteria instanceof co.dewald.guardian.dto.Role) {
                query = em.createNamedQuery(Permission.QUERY_BY_ROLE, Permission.class);
                query.setParameter(Role.PARAM_ROLE, criteria.getId());
            } else if (criteria instanceof User) {
                query = em.createNamedQuery(Permission.QUERY_BY_SUBJECT, Permission.class);
                query.setParameter(Subject.PARAM_USERNAME, criteria.getId());
            } else return null;
            
            return fetch(query);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public co.dewald.guardian.dto.Permission find(String id) {
        return find(getId(id));
    }

    @Override
    public co.dewald.guardian.dto.Permission find(co.dewald.guardian.dto.Permission id) {
        Permission permission = findPermission(id.getResource(), id.getAction());
        return MODEL2DTO.apply(permission);
    }

    @Override
    public String create(co.dewald.guardian.dto.Permission dto) {
        try {
            realm.create(DTO2MODEL.apply(dto));
            return getId(dto);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Boolean update(String id, co.dewald.guardian.dto.Permission dto) {
        return update(getId(id), dto);
    }

    @Override
    public Boolean update(co.dewald.guardian.dto.Permission id, co.dewald.guardian.dto.Permission dto) {
        Permission permission = findPermission(id.getResource(), id.getAction());
        if (permission == null) return null;
        
        try {
            permission.setResource(dto.getResource());
            permission.setAction(dto.getAction());
            permission.setActive(dto.isActive());
            permission.setBypass(dto.isBypass());
            
            realm.update(permission);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean delete(String id) {
        return delete(getId(id));
    }

    @Override
    public Boolean delete(co.dewald.guardian.dto.Permission id) {
        Permission permission = findPermission(id.getResource(), id.getAction());
        if (permission == null) return null;
        
        try {
            realm.remove(permission);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public <R extends DTO> Boolean linkReference(boolean link, co.dewald.guardian.dto.Permission id, R reference) {
        try {
            if (reference instanceof co.dewald.guardian.dto.Role) {
                realm.linkRolePermission(link, id.getResource(), id.getAction(), reference.getId());
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        
        return null;
    }

    @Grant(check = false)
    Permission findPermission(String resource, String action) {
        try {
            return realm.findPermissionBy(resource, action);
        } catch (Exception e) {
            return null;
        }
    }
}
