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
@Path(RoleResource.PATH_RESOURCE)
public interface RoleResource extends Resource<Role> {
    
    public static final String PATH_RESOURCE = "/roles";
    public static final String PATH_PARAM_ID = "role";
    public static final String PATH_ID = "/{" + PATH_PARAM_ID + "}";
    
    @Path(PATH_ID + UserResource.PATH_RESOURCE)
    UserResource subUsers(@PathParam(value = PATH_PARAM_ID) String role);

    @Path(PATH_ID + PermissionResource.PATH_RESOURCE)
    PermissionResource subPermissions(@PathParam(value = PATH_PARAM_ID) String role);

    @GET 
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response get();

    @GET @Path(PATH_ID)
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response get(@PathParam(PATH_PARAM_ID) String role);

    @DELETE @Path(PATH_ID)
    @Override
    Response delete(@PathParam(PATH_PARAM_ID) String role);

    @PUT @Path(PATH_ID)
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response put(@PathParam(PATH_PARAM_ID) String id, Role role);

    @POST 
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response post(Role role);
    
    @POST 
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    Response postByUser(@PathParam(UserResource.PATH_PARAM_ID) String username, Role role);
    
}
