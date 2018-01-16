package co.dewald.guardian.service.administration.rest;


import javax.ejb.EJB;

import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.User;
import co.dewald.guardian.service.administration.rest.resource.PermissionResource;
import co.dewald.guardian.service.administration.rest.resource.RoleResource;
import co.dewald.guardian.service.administration.rest.resource.UserResource;
import co.dewald.guardian.service.rest.BridgeResource;


/**
 * @author Dewald Pretorius
 *
 */
public class Users extends BridgeResource<User> implements UserResource {

    @EJB(beanName = "UserDAO") DAO<User> userDAO;
    
    @Override
    protected DAO<User> getDAO() {
        return userDAO;
    }

    @Override
    public RoleResource subRoles(String username) {
        return delegate(username, Roles.class);
    }

    @Override
    public PermissionResource subPermissions(String username) {
        return delegate(username, Permissions.class);
    }
}
