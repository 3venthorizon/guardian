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
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import co.dewald.guardian.dto.Role;


@Path("roles")
public interface RoleResource {

    @Path("{group}/users")
    default UserResource delegateUsers(@Context ResourceContext resourceContext, 
                                       @PathParam(value = "group") String group) {
        find(group);
        return resourceContext.getResource(UserResource.class);
    }
    
    @Path("{group}/permissions")
    default PermissionResource delegatePermissions(@Context ResourceContext resourceContext, 
                                                   @PathParam(value = "group") String group) {
        find(group);
        return resourceContext.getResource(PermissionResource.class);
    }

    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    List<Role> fetch();

    @GET @Path("{group}")
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    Role find(@PathParam(value = "group") String group);

    @DELETE @Path("{group}")
    void delete(@PathParam(value = "group") String group);

    @PUT @Path("{group}")
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    void update(@PathParam(value = "group") String group, Role role);

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    void create(Role role);

}
