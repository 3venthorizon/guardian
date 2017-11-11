package co.dewald.guardian.rest.gateway;


import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import co.dewald.guardian.gate.Guardian;


/**
 * 
 * @author Dewald Pretorius
 */
@Path("/")
public class GuardianResource {
    
    @EJB(beanName = "GuardianCore") Guardian guardian;

    @GET @Path("hello/{username}/{password}")
    public String helloWorld(@PathParam(value = "username") String username, 
                             @PathParam(value = "password") String password) {
        Boolean success = guardian.authenticate(username, password);
        
        if (Boolean.TRUE.equals(success) == false) return "Authentication Failed";
        
        return "Hello World";
    }
}
