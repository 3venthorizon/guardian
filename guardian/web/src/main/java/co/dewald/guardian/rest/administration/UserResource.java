package co.dewald.guardian.rest.administration;


import static javax.ws.rs.core.MediaType.*;

import java.util.List;

import javax.ejb.EJB;
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

import co.dewald.guardian.dto.User;


/**
 * @author Dewald Pretorius
 *
 */
@Path("users")
public class UserResource {
    
    @Context ResourceContext resourceContext;
    @EJB co.dewald.guardian.admin.UserResource userDAO;
    
    @Path("{username}/roles")
    public RoleResource delegateRoles(@PathParam(value = "username") String username) {
        userDAO.find(username); //existence check
        return resourceContext.getResource(RoleResource.class);
    }
    
    @Path("{username}/permissions")
    public PermissionResource delegatePermissions(@PathParam(value = "username") String username) {
        userDAO.find(username); //existence check
        return resourceContext.getResource(PermissionResource.class);
    }
    
    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    public List<User> fetch() {
        return userDAO.fetch();
    }
    
    @GET @Path("{username}")
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    public User find(@PathParam(value = "username") String username) {
        return userDAO.find(username);
    }

    @DELETE @Path("{username}")
    public void delete(@PathParam(value = "username") String username) {
        userDAO.delete(username);
    }

    @PUT @Path("{username}")
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    public void update(@PathParam(value = "username") String username, @QueryParam("user") User user) {
        userDAO.update(username, user);
    }

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    public void create(User user) {
        userDAO.create(user);
    }
}
