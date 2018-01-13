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
    
    public static final String PATH_ID = "{group}";
    
    @Path(PATH_ID + "/users")
    UserResource subUsers(@PathParam(value = "group") String group);

    @Path(PATH_ID + "/permissions")
    PermissionResource subPermissions(@PathParam(value = "group") String group);

    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response get();

    @GET @Path(PATH_ID)
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response get(@PathParam(value = "group") String group);

    @DELETE @Path(PATH_ID)
    @Override
    Response delete(@PathParam(value = "group") String group);

    @PUT @Path(PATH_ID)
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response put(@PathParam(value = "group") String group, Role role);

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response post(Role role);
}
