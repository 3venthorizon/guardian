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

import co.dewald.guardian.dto.User;
import co.dewald.guardian.service.rest.Resource;


/**
 * This interface services to document the specific URI paths and HTTP methods by which the UserResource can be 
 * accessed, using annotations.
 * 
 * @author Dewald Pretorius
 */
@Path("users")
public interface UserResource extends Resource<User> {
    
    public static final String PATH_ID = "{username}";

    @Path(PATH_ID + "/roles")
    RoleResource subRoles(@PathParam(value = "username") String username);

    @Path(PATH_ID + "/permissions")
    PermissionResource subPermissions(@PathParam(value = "username") String username);

    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response get();
    
    @GET @Path(PATH_ID)
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response get(@PathParam(value = "username") String username);

    @DELETE @Path(PATH_ID)
    @Override
    Response delete(@PathParam(value = "username") String username);

    @PUT @Path(PATH_ID)
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response put(@PathParam(value = "username") String username, User user);

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response post(User user);

}
