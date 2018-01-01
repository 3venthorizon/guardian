package co.dewald.guardian.service.gateway.rest;


import static javax.ws.rs.core.Response.Status.*;

import javax.ejb.EJB;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import co.dewald.guardian.gate.Guardian;


/**
 * 
 * @author Dewald Pretorius
 */
@Path("/")
public class GuardianResource {
    
    @EJB(beanName = "GuardianCore") Guardian guardian;

    @POST @Path("authenticate/{username}")
    public Response authenticate(@PathParam(value = "username") String username, 
                                 @HeaderParam(value = "password") String password) {
        Boolean success = guardian.authenticate(username, password);
        
        if (Boolean.TRUE.equals(success) == false) return Response.status(UNAUTHORIZED)
                                                                  .entity("Authentication Failed")
                                                                  .build();
        
        String token = guardian.getSessionToken(username);
        return Response.ok(token)
                       .build();
    }
}
