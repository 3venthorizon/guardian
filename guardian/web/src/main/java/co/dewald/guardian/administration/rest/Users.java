package co.dewald.guardian.administration.rest;


import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import co.dewald.guardian.admin.dao.UserDAO;
import co.dewald.guardian.administration.rest.resource.PermissionResource;
import co.dewald.guardian.administration.rest.resource.RoleResource;
import co.dewald.guardian.administration.rest.resource.UserResource;
import co.dewald.guardian.dto.User;


/**
 * @author Dewald Pretorius
 *
 */
public class Users implements UserResource {
    
    @Context ResourceContext resourceContext;
    @EJB UserDAO userDAO;
    
    Response roleResponse;
    Response permissionResponse;

    @Override
    public List<User> fetch() {
        return userDAO.fetch();
    }
    
    @Override
    public Response find(String username) {
        User user =  userDAO.find(username);
        if (user == null) return Response.status(Status.NOT_FOUND).build();
        
        return Response.ok(user).build();
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
        Roles roles = resourceContext.getResource(Roles.class);
        roles.userResponse = find(username);
        
        return roles;
    }

    @Override
    public PermissionResource linkPermissions(String username) {
        Permissions permissions = resourceContext.getResource(Permissions.class);
        permissions.userResponse = find(username);
        
        return permissions;
    }
}
