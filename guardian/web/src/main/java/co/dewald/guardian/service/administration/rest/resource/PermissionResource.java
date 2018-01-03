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

import co.dewald.guardian.dto.Permission;


/**
 * 
 * @author Dewald Pretorius
 */
@Path("permissions")
public interface PermissionResource {

    @Path("{resource}:{action}/users")
    UserResource linkUsers(@PathParam(value = "resource") String resource, 
                           @PathParam(value = "action") String action);

    @Path("{resource}:{action}/roles")
    RoleResource linkRoles(@PathParam(value = "resource") String resource, 
                           @PathParam(value = "action") String action);

    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    Response fetch();

    @GET @Path("{resource}:{action}")
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    Response find(@PathParam(value = "resource") String resource, 
                    @PathParam(value = "action") String action);

    @DELETE @Path("{resource}:{action}")
    Response delete(@PathParam(value = "resource") String resource, 
                    @PathParam(value = "action") String action);

    @PUT
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    Response update(@PathParam(value = "resource") String resource, 
                    @PathParam(value = "action") String action, Permission permission);

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    Response create(Permission permission);

}
