package co.dewald.guardian.service.administration.rest;


import javax.ejb.EJB;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import co.dewald.guardian.admin.dao.UserDAO;
import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.User;
import co.dewald.guardian.service.administration.rest.resource.PermissionResource;
import co.dewald.guardian.service.administration.rest.resource.RoleResource;
import co.dewald.guardian.service.administration.rest.resource.UserResource;
import co.dewald.guardian.service.rest.BaseResource;


/**
 * @author Dewald Pretorius
 *
 */
public class Users extends BaseResource<User> implements UserResource {
    
    @Context ResourceContext resourceContext;
    @Context UriInfo uriInfo;
    @EJB UserDAO userDAO;
    
    Response roleResponse;
    Response permissionResponse;
    
    @Override
    protected DAO<User> getDAO() {
        return userDAO;
    }

    @Override
    protected UriInfo getUriInfo() {
        return uriInfo;
    }

    @Override
    public RoleResource subRoles(String username) {
        Roles roles = resourceContext.getResource(Roles.class);
        roles.userResponse = find(username);
        
        return roles;
    }

    @Override
    public PermissionResource subPermissions(String username) {
        Permissions permissions = resourceContext.getResource(Permissions.class);
        permissions.userResponse = find(username);
        
        return permissions;
    }
}
