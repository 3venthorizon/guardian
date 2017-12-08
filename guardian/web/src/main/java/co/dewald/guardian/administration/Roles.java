package co.dewald.guardian.administration;


import java.util.List;

import javax.ejb.EJB;

import co.dewald.guardian.admin.dao.RoleDAO;
import co.dewald.guardian.administration.rest.RoleResource;
import co.dewald.guardian.dto.Role;


/**
 * @author Dewald Pretorius
 *
 */
public class Roles implements RoleResource {
    
    @EJB RoleDAO roleDAO;
    
    @Override
    public List<Role> fetch() {
        return roleDAO.fetch();
    }
    
    @Override
    public Role find(String group) {
        return roleDAO.find(group);
    }

    @Override
    public void delete(String group) {
        roleDAO.delete(group);
    }

    @Override
    public void update(String group, Role role) {
        roleDAO.update(group, role);
    }

    @Override
    public void create(Role role) {
        roleDAO.create(role);
    }
}
