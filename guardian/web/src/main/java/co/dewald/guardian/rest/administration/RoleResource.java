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

import co.dewald.guardian.dto.Role;


/**
 * @author Dewald Pretorius
 *
 */
@Path("roles")
public class RoleResource {
    
    @Context ResourceContext resourceContext;
    @EJB co.dewald.guardian.admin.RoleResource roleDAO;
    
    @Path("{group}/users")
    public UserResource delegateUsers(@PathParam(value = "group") String group) {
        roleDAO.find(group); //existence check
        return resourceContext.getResource(UserResource.class);
    }
    
    @Path("{group}/permissions")
    public PermissionResource delegatePermissions(@PathParam(value = "group") String group) {
        roleDAO.find(group); //existence check
        return resourceContext.getResource(PermissionResource.class);
    }
    
    @GET
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    public List<Role> fetch() {
        return roleDAO.fetch();
    }
    
    @GET @Path("{group}")
    @Produces(value = {APPLICATION_JSON, APPLICATION_XML})
    public Role find(@PathParam(value = "group") String group) {
        return roleDAO.find(group);
    }

    @DELETE @Path("{group}")
    public void delete(@PathParam(value = "group") String group) {
        roleDAO.delete(group);
    }

    @PUT @Path("{group}")
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    public void update(@PathParam(value = "group") String group, @QueryParam("role") Role role) {
        roleDAO.update(group, role);
    }

    @POST
    @Consumes(value = {APPLICATION_JSON, APPLICATION_XML})
    public void create(Role role) {
        roleDAO.create(role);
    }
}
