package co.dewald.guardian.administration.rest;


import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import co.dewald.guardian.admin.dao.UserDAO;
import co.dewald.guardian.administration.rest.resource.PermissionResource;
import co.dewald.guardian.administration.rest.resource.RoleResource;
import co.dewald.guardian.administration.rest.resource.UserResource;
import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.dto.Role;
import co.dewald.guardian.dto.User;


/**
 * @author Dewald Pretorius
 *
 */
public class Users implements UserResource {
    
    @Context ResourceContext resourceContext;
    @EJB UserDAO userDAO;

    @Override
    public List<User> fetch() {
        return userDAO.fetch();
    }
    
    @Override
    public User find(String username) {
        return userDAO.find(username);
    }

    @Override
    public void delete(String username) {
        userDAO.delete(username);
    }

    @Override
    public void update(String username, User user) {
        userDAO.update(username, user);
    }

    @Override
    public void create(User user) {
        userDAO.create(user);
    }

    @Override
    public RoleResource linkRoles(String username) {
        User user = find(username);
        return resourceContext.getResource(Roles.class);
    }

    @Override
    public PermissionResource linkPermissions(String username) {
        User user = find(username);
        return resourceContext.getResource(Permissions.class);
    }
}
