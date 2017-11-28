package co.dewald.guardian.rest.administration;


import static javax.ws.rs.core.MediaType.*;

import javax.ejb.EJB;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import co.dewald.guardian.dto.User;
import co.dewald.guardian.gate.AdminResource;


/**
 * @author Dewald Pretorius
 *
 */
@Path("users")
public class UserResource {
    
    @EJB(beanName = "UserDAO") AdminResource<User, String> userDAO;
    
    @GET @Path("{username}")
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    public User find(@PathParam(value = "username") String uniqueKey) {
        return userDAO.find(uniqueKey);
    }

    @POST
    public void create(User resource) {
        userDAO.create(resource);
    }

    @PUT
    public void update(User resource) {
        userDAO.update(resource);
    }

    @DELETE
    public void delete(User resource) {
        userDAO.delete(resource);
    }

}
