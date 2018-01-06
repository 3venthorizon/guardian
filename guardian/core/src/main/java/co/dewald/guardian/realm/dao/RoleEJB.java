package co.dewald.guardian.realm.dao;


import static co.dewald.guardian.realm.Permission.PARAM_RESOURCE;
import static co.dewald.guardian.realm.Permission.PARAM_ACTION;
import static co.dewald.guardian.realm.Subject.PARAM_USERNAME;

import java.util.List;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import co.dewald.guardian.admin.dao.RoleDAO;
import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.dto.User;
import co.dewald.guardian.gate.Grant;
import co.dewald.guardian.gate.Guard;
import co.dewald.guardian.realm.Role;


/**
 * @author Dewald Pretorius
 */
//FIXME @Guard
@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateless(name = "RoleDAO")
public class RoleEJB implements Model2DTO<Role, co.dewald.guardian.dto.Role>, RoleDAO {

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
    public Function<Role, co.dewald.guardian.dto.Role> model2dto() {
        return MODEL2DTO;
    }

    @Override
    public List<co.dewald.guardian.dto.Role> fetch() {
        TypedQuery<Role> query = em.createNamedQuery(Role.QUERY_ALL, Role.class);
        return fetch(query);
    }

    @Override
    public List<co.dewald.guardian.dto.Role> fetchBy(User user) {
        TypedQuery<Role> query = em.createNamedQuery(Role.QUERY_BY_SUBJECT, Role.class);
        query.setParameter(PARAM_USERNAME, user.getId());
        
        return fetch(query);
    }
    
    @Override
    public List<co.dewald.guardian.dto.Role> fetchBy(Permission permission) {
        TypedQuery<Role> query = em.createNamedQuery(Role.QUERY_BY_PERMISSION, Role.class);
        query.setParameter(PARAM_RESOURCE, permission.getResource());
        query.setParameter(PARAM_ACTION, permission.getAction());
        
        return fetch(query);
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
    public boolean link(boolean link, co.dewald.guardian.dto.Role dto, User user) {
        try {
            realm.linkUserRole(link, user.getId(), dto.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean link(boolean link, co.dewald.guardian.dto.Role dto, Permission permission) {
        try {
            realm.linkRolePermission(link, dto.getId(), permission.getResource(), permission.getAction());
            return true;
        } catch (Exception e) {
            return false;
        }
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
