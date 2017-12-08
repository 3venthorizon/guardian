package co.dewald.guardian.administration;


import java.util.List;

import javax.ejb.EJB;

import co.dewald.guardian.admin.dao.UserDAO;
import co.dewald.guardian.administration.rest.UserResource;
import co.dewald.guardian.dto.User;


/**
 * @author Dewald Pretorius
 *
 */
public class Users implements UserResource {
    
    @EJB UserDAO userDAO;

    @Override
    public List<User> fetch() {
        return userDAO.fetch();
    }
    
    @Override
    public User find(String username) {
        return userDAO.find(username);
    }

    @Override
    public void delete(String username) {
        userDAO.delete(username);
    }

    @Override
    public void update(String username, User user) {
        userDAO.update(username, user);
    }

    @Override
    public void create(User user) {
        userDAO.create(user);
    }
}
