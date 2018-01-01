package co.dewald.guardian.service.administration.rest;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import co.dewald.guardian.admin.dao.UserDAO;
import co.dewald.guardian.dto.User;
import co.dewald.guardian.service.administration.rest.resource.PermissionResource;
import co.dewald.guardian.service.administration.rest.resource.RoleResource;
import co.dewald.guardian.service.administration.rest.resource.UserResource;
import co.dewald.guardian.service.rest.Resource;


/**
 * @author Dewald Pretorius
 *
 */
public class Users extends Resource<User> implements UserResource {
    
    @Context ResourceContext resourceContext;
    @EJB UserDAO userDAO;
    
    Response roleResponse;
    Response permissionResponse;
    
    @PostConstruct
    @Override
    protected void initDAO() {
        dao = userDAO;
    }

    @Override
    public List<User> fetch() {
        return userDAO.fetch();
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
