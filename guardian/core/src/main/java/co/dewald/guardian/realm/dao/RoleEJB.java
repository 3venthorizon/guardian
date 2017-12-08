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
        co.dewald.guardian.dto.Role dto = new co.dewald.guardian.dto.Role();
        dto.setGroup(model.getGroup());
        
        return dto;
    };
    
    static final Function<co.dewald.guardian.dto.Role, Role> DTO2MODEL = dto -> {
        Role role = new Role();
        role.setGroup(dto.getGroup());
        
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
        query.setParameter(PARAM_USERNAME, user.getUsername());
        
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
        Role role = realm.findRoleBy(group);
        return MODEL2DTO.apply(role);
    }

    @Override
    public void delete(String group) {
        Role role = realm.findRoleBy(group);
        realm.remove(role);
    }

    @Override
    public void update(String group, co.dewald.guardian.dto.Role dto) {
        Role role = realm.findRoleBy(group);
        role.setGroup(dto.getGroup());
        
        realm.update(role);
    }

    @Override
    public void create(co.dewald.guardian.dto.Role dto) {
        realm.create(DTO2MODEL.apply(dto));
    }

    @Override
    public void link(boolean link, co.dewald.guardian.dto.Role dto, User user) {
        realm.linkUserRole(link, user.getUsername(), dto.getGroup());
    }

    @Override
    public void link(boolean link, co.dewald.guardian.dto.Role dto, Permission permission) {
        realm.linkRolePermission(link, dto.getGroup(), permission.getResource(), permission.getAction());
    }
}
