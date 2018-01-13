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
import co.dewald.guardian.service.rest.Resource;


/**
 * 
 * @author Dewald Pretorius
 */
@Path("permissions")
public interface PermissionResource extends Resource<Permission> {
    
    public static final String PATH_ID = "{resource}:{action}";

    @Path(PATH_ID + "/users")
    UserResource subUsers(@PathParam(value = "resource") String resource, 
                          @PathParam(value = "action") String action);

    @Path(PATH_ID + "/roles")
    RoleResource subRoles(@PathParam(value = "resource") String resource, 
                          @PathParam(value = "action") String action);

    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    Response get();

    @GET @Path(PATH_ID)
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    default Response find(@PathParam(value = "resource") String resource, 
                          @PathParam(value = "action") String action) {
        String id = composeID(resource, action);
        return get(id);
    }

    @DELETE @Path(PATH_ID)
    default Response delete(@PathParam(value = "resource") String resource, 
                            @PathParam(value = "action") String action) {
        String id = composeID(resource, action);
        return delete(id);
    }

    @PUT @Path(PATH_ID)
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    default Response update(@PathParam(value = "resource") String resource, 
                    @PathParam(value = "action") String action, Permission permission) {
        String id = composeID(resource, action);
        return put(id, permission);
    }

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    @Override
    Response post(Permission permission);
    
    default String composeID(String resource, String action) {
        return resource + ':' + action;
    }
}
