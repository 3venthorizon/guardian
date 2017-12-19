package co.dewald.guardian.administration.rest.resource;


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
import javax.ws.rs.core.Response;

import co.dewald.guardian.dto.User;


/**
 * 
 * @author Dewald Pretorius
 */
@Path("users")
public interface UserResource {

    @Path("{username}/roles")
    RoleResource linkRoles(@PathParam(value = "username") String username);

    @Path("{username}/permissions")
    PermissionResource linkPermissions(@PathParam(value = "username") String username);

    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    List<User> fetch();
    
    @GET @Path("{username}")
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    Response find(@PathParam(value = "username") String username);

    @DELETE @Path("{username}")
    void delete(@PathParam(value = "username") String username);

    @PUT @Path("{username}")
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    void update(@PathParam(value = "username") String username, User user);

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    void create(User user);

}
