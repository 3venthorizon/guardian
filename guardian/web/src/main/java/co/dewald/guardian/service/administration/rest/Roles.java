package co.dewald.guardian.service.administration.rest;


import javax.ejb.EJB;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import co.dewald.guardian.admin.dao.RoleDAO;
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
    protected void initDAO() {
        dao = roleDAO;
    }

    @Override
    protected UriInfo getUriInfo() {
        return uriInfo;
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
