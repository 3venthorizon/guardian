package co.dewald.guardian.service.administration.rest;


import javax.ejb.EJB;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import co.dewald.guardian.admin.dao.PermissionDAO;
import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.service.administration.rest.resource.PermissionResource;
import co.dewald.guardian.service.administration.rest.resource.RoleResource;
import co.dewald.guardian.service.administration.rest.resource.UserResource;
import co.dewald.guardian.service.rest.BaseResource;


/**
 * @author Dewald Pretorius
 */
public class Permissions extends BaseResource<Permission> implements PermissionResource {
    
    @Context ResourceContext resourceContext;
    @Context UriInfo uriInfo;
    @EJB PermissionDAO permissionDAO;
    
    Response userResponse;
    Response roleResponse;

    @Override
    protected void initDAO() {
        dao = permissionDAO;
    }
    
    @Override
    protected UriInfo getUriInfo() {
        return uriInfo;
    }

    @Override
    public UserResource linkUsers(String resource, String action) {
        find(resource, action);
        return resourceContext.getResource(Users.class);
    }

    @Override
    public RoleResource linkRoles(String resource, String action) {
        find(resource, action);
        return resourceContext.getResource(Roles.class);
    }

    @Override
    public Response find(String resource, String action) {
        String id = composeID(resource, action);
        return super.find(id);
    }

    @Override
    public Response delete(String resource, String action) {
        String id = composeID(resource, action);
        return super.delete(id);
    }

    @Override
    public Response update(String resource, String action, Permission permission) {
        String id = composeID(resource, action);
        return super.update(id, permission);
    }
    
    String composeID(String resource, String action) {
        return resource + ':' + action;
    }
}
