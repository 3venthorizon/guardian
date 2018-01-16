package co.dewald.guardian.service.administration.rest;


import javax.ejb.EJB;

import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.service.administration.rest.resource.PermissionResource;
import co.dewald.guardian.service.administration.rest.resource.RoleResource;
import co.dewald.guardian.service.administration.rest.resource.UserResource;
import co.dewald.guardian.service.rest.BridgeResource;


/**
 * @author Dewald Pretorius
 */
public class Permissions extends BridgeResource<Permission> implements PermissionResource {
    
    @EJB(beanName = "PermissionDAO") DAO<Permission> permissionDAO;
    
    @Override
    protected DAO<Permission> getDAO() {
        return permissionDAO;
    }

    @Override
    public UserResource subUsers(String resource, String action) {
        String id = composeID(resource, action);
        return delegate(id, Users.class);
    }

    @Override
    public RoleResource subRoles(String resource, String action) {
        String id = composeID(resource, action);
        return delegate(id, Roles.class);
    }    
}
