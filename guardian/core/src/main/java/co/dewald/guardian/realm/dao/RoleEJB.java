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
@Stateless(name = "RoleDAO")
public class RoleEJB implements Model2DTO<Role, co.dewald.guardian.dto.Role>, DAO<co.dewald.guardian.dto.Role> {

    @PersistenceContext(unitName = "realm") EntityManager em;
    @EJB RealmDAO realm;
    
    static final Function<Role, co.dewald.guardian.dto.Role> MODEL2DTO = model -> {
        if (model == null) return null;
        
        co.dewald.guardian.dto.Role dto = new co.dewald.guardian.dto.Role();
        dto.setId(model.getGroup());
        
        return dto;
    };
    
    static final Function<co.dewald.guardian.dto.Role, Role> DTO2MODEL = dto -> {
        Role role = new Role();
        role.setGroup(dto.getId());
        
        return role;
    };
    
    @Override
    public String getId(co.dewald.guardian.dto.Role id) {
        return id.getId();
    }

    @Override
    public co.dewald.guardian.dto.Role getId(String id) {
        co.dewald.guardian.dto.Role roleId = new co.dewald.guardian.dto.Role();
        roleId.setId(id);
        
        return roleId;
    }

    @Override
    public Function<Role, co.dewald.guardian.dto.Role> model2dto() {
        return MODEL2DTO;
    }

    @Override
    public List<co.dewald.guardian.dto.Role> fetch() {
        TypedQuery<Role> query = em.createNamedQuery(Role.QUERY_ALL, Role.class);
        return fetch(query);
    }

    @Override
    public <C extends DTO> List<co.dewald.guardian.dto.Role> fetchBy(C criteria) {
        try {
            TypedQuery<Role> query;
            
            if (criteria instanceof co.dewald.guardian.dto.Permission) {
                co.dewald.guardian.dto.Permission permissionCriteria = (co.dewald.guardian.dto.Permission) criteria;
                query = em.createNamedQuery(Role.QUERY_BY_PERMISSION, Role.class);
                query.setParameter(Permission.PARAM_RESOURCE, permissionCriteria.getResource());
                query.setParameter(Permission.PARAM_ACTION, permissionCriteria.getAction());
            } else if (criteria instanceof User) {
                query = em.createNamedQuery(Role.QUERY_BY_SUBJECT, Role.class);
                query.setParameter(Subject.PARAM_USERNAME, criteria.getId());
            } else return null;
            
            return fetch(query);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public co.dewald.guardian.dto.Role find(String group) {
        Role role = findRole(group);
        return MODEL2DTO.apply(role);
    }

    @Override
    public Boolean delete(String group) {
        Role role = findRole(group);
        if (role == null) return null;
        
        try {
            realm.remove(role);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean update(String group, co.dewald.guardian.dto.Role dto) {
        Role role = findRole(group);
        if (role == null) return null;
        
        try {
            role.setGroup(dto.getId());
            
            realm.update(role);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String create(co.dewald.guardian.dto.Role dto) {
        try {
            realm.create(DTO2MODEL.apply(dto));
            return dto.getId();
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public <R extends DTO> Boolean linkReference(boolean link, co.dewald.guardian.dto.Role id, R reference) {
        try {
            if (reference instanceof User) {
                realm.linkUserRole(link, id.getId(), reference.getId());
                return true;
            }
            
            if (reference instanceof co.dewald.guardian.dto.Permission) {
                co.dewald.guardian.dto.Permission permission = (co.dewald.guardian.dto.Permission) reference;
                realm.linkRolePermission(link, id.getId(), permission.getResource(), permission.getAction());
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        
        return null;
    }

    @Grant(check = false)
    Role findRole(String group) {
        try {
            return realm.findRoleBy(group);
        } catch (Exception e) {
            return null;
        }
    }
}
