package co.dewald.guardian.service.administration.rest.resource;


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

import co.dewald.guardian.dto.Role;


/**
 * 
 * @author Dewald Pretorius
 */
@Path("roles")
public interface RoleResource {
    
    @Path("{group}/users")
    UserResource linkUserResource(@PathParam(value = "group") String group);

    @Path("{group}/permissions")
    PermissionResource linkPermissions(@PathParam(value = "group") String group);

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
