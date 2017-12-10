package co.dewald.guardian.admin.dao;


import java.util.List;

import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.dto.Role;
import co.dewald.guardian.dto.User;


/**
 * @author Dewald Pretorius
 */
public interface PermissionDAO extends DAO<Permission, Permission> {

    List<Permission> fetchBy(Role role);
    
    List<Permission> fetchBy(User user);
    
    void link(boolean link, Permission permission, Role role);
}
