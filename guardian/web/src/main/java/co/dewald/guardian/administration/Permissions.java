package co.dewald.guardian.administration;


import java.util.List;

import javax.ejb.EJB;

import co.dewald.guardian.admin.dao.PermissionDAO;
import co.dewald.guardian.administration.rest.PermissionResource;
import co.dewald.guardian.dto.Permission;


/**
 * @author Dewald Pretorius
 *
 */
public class Permissions implements PermissionResource {
    
    @EJB PermissionDAO permissionDAO;
    
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
}
