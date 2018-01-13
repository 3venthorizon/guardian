package co.dewald.guardian.service.administration.rest;


import javax.ejb.EJB;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.service.administration.rest.resource.PermissionResource;
import co.dewald.guardian.service.administration.rest.resource.RoleResource;
import co.dewald.guardian.service.administration.rest.resource.UserResource;
import co.dewald.guardian.service.rest.BridgeResource;


/**
 * @author Dewald Pretorius
 */
public class Permissions extends BridgeResource<Permission> implements PermissionResource {
    
    @Context ResourceContext resourceContext;
    @Context UriInfo uriInfo;
    @EJB(beanName = "PermissionDAO") DAO<Permission> permissionDAO;
    
    Response userResponse;
    Response roleResponse;

    @Override
    protected DAO<Permission> getDAO() {
        return permissionDAO;
    }
    
    @Override
    protected UriInfo getUriInfo() {
        return uriInfo;
    }

    @Override
    public UserResource subUsers(String resource, String action) {
        Users users = resourceContext.getResource(Users.class);
        
        return users;
    }

    @Override
    public RoleResource subRoles(String resource, String action) {
        Roles roles = resourceContext.getResource(Roles.class);
        
        return roles;
    }    
}
