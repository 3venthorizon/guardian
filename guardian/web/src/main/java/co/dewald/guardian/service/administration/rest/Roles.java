package co.dewald.guardian.service.administration.rest;


import javax.ejb.EJB;
import javax.ws.rs.core.Response;

import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.Role;
import co.dewald.guardian.service.administration.rest.resource.PermissionResource;
import co.dewald.guardian.service.administration.rest.resource.RoleResource;
import co.dewald.guardian.service.administration.rest.resource.UserResource;
import co.dewald.guardian.service.rest.BridgeResource;


/**
 * @author Dewald Pretorius
 *
 */
public class Roles extends BridgeResource<Role> implements RoleResource {
    
    @EJB(beanName = "RoleDAO") DAO<Role> roleDAO;
    
    @Override
    protected DAO<Role> getDAO() {
        return roleDAO;
    }

    @Override
    public UserResource subUsers(String group) {
        return delegate(group, Users.class);
    }

    @Override
    public PermissionResource subPermissions(String group) {
        return delegate(group, Permissions.class);
    }

    @Override
    public Response postByUser(String username, Role role) {
        return super.post(role);
    }
}
