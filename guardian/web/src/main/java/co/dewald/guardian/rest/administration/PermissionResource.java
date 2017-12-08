package co.dewald.guardian.rest.administration;


import static javax.ws.rs.core.MediaType.*;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import co.dewald.guardian.dto.Permission;


/**
 * @author Dewald Pretorius
 *
 */
@Path("permissions")
public class PermissionResource {
    
    @Context ResourceContext resourceContext;
    @EJB co.dewald.guardian.admin.PermissionResource permissionDAO;
    
    @Path("{resource}:{action}/users")
    public UserResource delegateUsers(@PathParam(value = "resource") String resource, 
                                      @PathParam(value = "action") String action) {
        Permission dto = new Permission();
        dto.setResource(resource);
        dto.setAction(action);
        permissionDAO.find(dto); //existence check
        
        return resourceContext.getResource(UserResource.class);
    }
    
    @Path("{resource}:{action}/roles")
    public RoleResource delegateRoles(@PathParam(value = "resource") String resource, 
                                      @PathParam(value = "action") String action) {
        Permission dto = new Permission();
        dto.setResource(resource);
        dto.setAction(action);
        permissionDAO.find(dto); //existence check
        
        return resourceContext.getResource(RoleResource.class);
    }
    
    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    public List<Permission> fetch() {
        return permissionDAO.fetch();
    }
    
    @GET @Path("{resource}:{action}")
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    public Permission find(@PathParam(value = "resource") String resource, @PathParam(value = "action") String action) {
        Permission dto = new Permission();
        dto.setResource(resource);
        dto.setAction(action);
        
        return permissionDAO.find(dto);
    }

    @DELETE @Path("{resource}:{action}")
    public void delete(@PathParam(value = "resource") String resource, @PathParam(value = "action") String action) {
        Permission id = new Permission();
        id.setResource(resource);
        id.setAction(action);
        
        permissionDAO.delete(id);
    }

    @PUT
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    public void update(@PathParam(value = "resource") String resource, @PathParam(value = "action") String action,
                       @QueryParam("permission") Permission permission) {
        Permission id = new Permission();
        id.setResource(resource);
        id.setAction(action);

        permissionDAO.update(id, permission);
    }

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    public void create(Permission permission) {
        permissionDAO.create(permission);
    }
}
