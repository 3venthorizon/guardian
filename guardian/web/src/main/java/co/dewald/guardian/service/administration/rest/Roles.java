package co.dewald.guardian.service.administration.rest;


import javax.ejb.EJB;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import co.dewald.guardian.admin.dao.RoleDAO;
import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.Role;
import co.dewald.guardian.service.administration.rest.resource.PermissionResource;
import co.dewald.guardian.service.administration.rest.resource.RoleResource;
import co.dewald.guardian.service.administration.rest.resource.UserResource;
import co.dewald.guardian.service.rest.BaseResource;


/**
 * @author Dewald Pretorius
 *
 */
public class Roles extends BaseResource<Role> implements RoleResource {
    
    @Context ResourceContext resourceContext;
    @Context UriInfo uriInfo;
    @EJB RoleDAO roleDAO;
    
    Response userResponse;
    Response permissionResponse;
    
    @Override
    protected DAO<Role> getDAO() {
        return roleDAO;
    }

    @Override
    protected UriInfo getUriInfo() {
        return uriInfo;
    }

    @Override
    public UserResource subUsers(String group) {
        Users users = resourceContext.getResource(Users.class);
        users.roleResponse = find(group);
        
        return users;
    }

    @Override
    public PermissionResource subPermissions(String group) {
        Permissions permissions = resourceContext.getResource(Permissions.class);
        permissions.roleResponse = find(group);
        
        return permissions;
    }
}
