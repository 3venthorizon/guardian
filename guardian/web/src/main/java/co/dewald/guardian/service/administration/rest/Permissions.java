package co.dewald.guardian.service.administration.rest;


import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import co.dewald.guardian.admin.dao.PermissionDAO;
import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.service.administration.rest.resource.PermissionResource;
import co.dewald.guardian.service.administration.rest.resource.RoleResource;
import co.dewald.guardian.service.administration.rest.resource.UserResource;


/**
 * @author Dewald Pretorius
 *
 */
public class Permissions implements PermissionResource {
    
    @Context ResourceContext resourceContext;
    @EJB PermissionDAO permissionDAO;
    
    Response userResponse;
    Response roleResponse;
    
    @Override
    public List<Permission> fetch() {
        return permissionDAO.fetch();
    }
    
    @Override
    public Permission find(String resource, String action) {
        Permission dto = new Permission();
        dto.setResource(resource);
        dto.setAction(action);
        
        return permissionDAO.find(dto);
    }

    @Override
    public void delete(String resource, String action) {
        Permission id = new Permission();
        id.setResource(resource);
        id.setAction(action);
        
        permissionDAO.delete(id);
    }

    @Override
    public void update(String resource, String action, Permission permission) {
        Permission id = new Permission();
        id.setResource(resource);
        id.setAction(action);

        permissionDAO.update(id, permission);
    }

    @Override
    public void create(Permission permission) {
        permissionDAO.create(permission);
    }

    @Override
    public UserResource linkUsers(String resource, String action) {
        find(resource, action);
        return resourceContext.getResource(Users.class);
    }

    @Override
    public RoleResource linkRoles(String resource, String action) {
        find(resource, action);
        return resourceContext.getResource(Roles.class);
    }
}
