package co.dewald.guardian.administration.rest;


import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import co.dewald.guardian.admin.dao.RoleDAO;
import co.dewald.guardian.administration.rest.resource.PermissionResource;
import co.dewald.guardian.administration.rest.resource.RoleResource;
import co.dewald.guardian.administration.rest.resource.UserResource;
import co.dewald.guardian.dto.Role;


/**
 * @author Dewald Pretorius
 *
 */
public class Roles implements RoleResource {
    
    @Context ResourceContext resourceContext;
    @EJB RoleDAO roleDAO;
    
    Response userResponse;
    Response permissionResponse;
    
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

    @Override
    public UserResource linkUserResource(String group) {
        find(group);
        return resourceContext.getResource(Users.class);
    }

    @Override
    public PermissionResource linkPermissions(String group) {
        find(group);
        return resourceContext.getResource(Permissions.class);
    }
}
