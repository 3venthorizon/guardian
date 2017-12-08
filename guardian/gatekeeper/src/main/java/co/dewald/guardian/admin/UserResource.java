package co.dewald.guardian.admin;


import java.util.List;

import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.dto.Role;
import co.dewald.guardian.dto.User;


/**
 * @author Dewald Pretorius
 */
public interface UserResource extends AdminResource<User, String> {

    List<User> fetchBy(Role role);
    
    List<User> fetchBy(Permission permission);
    
    void link(boolean link, User user, Role role);
}
