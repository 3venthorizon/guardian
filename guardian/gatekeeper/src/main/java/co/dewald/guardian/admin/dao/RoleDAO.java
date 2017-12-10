package co.dewald.guardian.admin.dao;


import java.util.List;

import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.dto.Role;
import co.dewald.guardian.dto.User;


/**
 * @author Dewald Pretorius
 */
public interface RoleDAO extends DAO<Role, String> {

    List<Role> fetchBy(User user);
    
    List<Role> fetchBy(Permission permission);
    
    void link(boolean link, Role role, User user);
    
    void link(boolean link, Role role, Permission permission);
}
