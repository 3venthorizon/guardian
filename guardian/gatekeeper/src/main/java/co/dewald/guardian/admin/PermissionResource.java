package co.dewald.guardian.admin;


import java.util.List;

import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.dto.Role;
import co.dewald.guardian.dto.User;


/**
 * @author Dewald Pretorius
 */
public interface PermissionResource extends AdminResource<Permission, Permission> {

    List<Permission> fetchBy(Role role);
    
    List<Permission> fetchBy(User user);
    
    void link(boolean link, Permission permission, Role role);
}
