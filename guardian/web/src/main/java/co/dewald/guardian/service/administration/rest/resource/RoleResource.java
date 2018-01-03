package co.dewald.guardian.service.administration.rest.resource;


import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import co.dewald.guardian.dto.Role;
import co.dewald.guardian.service.rest.Resource;


/**
 * 
 * @author Dewald Pretorius
 */
@Path("roles")
public interface RoleResource extends Resource<Role> {
    
    @Path("{group}/users")
    UserResource linkUserResource(@PathParam(value = "group") String group);

    @Path("{group}/permissions")
    PermissionResource linkPermissions(@PathParam(value = "group") String group);

    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    Response fetch();

    @GET @Path("{group}")
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    Response find(@PathParam(value = "group") String group);

    @DELETE @Path("{group}")
    Response delete(@PathParam(value = "group") String group);

    @PUT @Path("{group}")
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    Response update(@PathParam(value = "group") String group, Role role);

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    Response create(Role role);
}
