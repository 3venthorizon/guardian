package co.dewald.guardian.administration.rest;


import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.util.List;

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


@Path("permissions")
public interface PermissionResource {

    @Path("{resource}:{action}/users")
    default UserResource delegateUsers(@Context ResourceContext resourceContext,
                               @PathParam(value = "resource") String resource, 
                               @PathParam(value = "action") String action) {
        find(resource, action);
        return resourceContext.getResource(UserResource.class);
    }

    @Path("{resource}:{action}/roles")
    default RoleResource delegateRoles(@Context ResourceContext resourceContext,
                               @PathParam(value = "resource") String resource, 
                               @PathParam(value = "action") String action) {
        find(resource, action);
        return resourceContext.getResource(RoleResource.class);
    }

    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    List<Permission> fetch();

    @GET @Path("{resource}:{action}")
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    Permission find(@PathParam(value = "resource") String resource, 
                    @PathParam(value = "action") String action);

    @DELETE @Path("{resource}:{action}")
    void delete(@PathParam(value = "resource") String resource, 
                @PathParam(value = "action") String action);

    @PUT
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    void update(@PathParam(value = "resource") String resource, 
                @PathParam(value = "action") String action, Permission permission);

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    void create(Permission permission);

}
