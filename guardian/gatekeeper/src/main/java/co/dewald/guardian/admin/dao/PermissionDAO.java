package co.dewald.guardian.admin.dao;


import java.util.List;
import java.util.function.Function;

import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.dto.Role;
import co.dewald.guardian.dto.User;


/**
 * @author Dewald Pretorius
 */
public interface PermissionDAO extends DAO<Permission> {
    
    public static final Function<String, Permission> ID = id -> {
        String[] compositeId = id.split(":");
        Permission permission = new Permission();
        permission.setResource(compositeId[0]);
        permission.setAction(compositeId[1]);
        
        return permission;
    };

    @Override
    default Permission find(String id) {
        return find(ID.apply(id));
    }
    
    @Override
    default Boolean update(String id, Permission dto) {
        return update(ID.apply(id), dto);
    } 
    
    @Override
    default Boolean delete(String id) {
        return delete(ID.apply(id));
    }
    
    List<Permission> fetchBy(Role role);
    
    List<Permission> fetchBy(User user);

    boolean link(boolean link, Permission permission, Role role);

}
