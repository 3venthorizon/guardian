package co.dewald.guardian.realm.dao;


import static co.dewald.guardian.realm.Permission.PARAM_RESOURCE;
import static co.dewald.guardian.realm.Permission.PARAM_ACTION;
import static co.dewald.guardian.realm.Role.PARAM_ROLE;

import java.util.List;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import co.dewald.guardian.admin.dao.UserDAO;
import co.dewald.guardian.dto.Permission;
import co.dewald.guardian.dto.Role;
import co.dewald.guardian.dto.User;
import co.dewald.guardian.gate.Grant;
import co.dewald.guardian.gate.Guard;
import co.dewald.guardian.realm.Subject;


/**
 * @author Dewald Pretorius
 */
//FIXME @Guard
@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateless(name = "UserDAO")
public class UserEJB implements Model2DTO<Subject, User>, UserDAO {

    @PersistenceContext(unitName = "realm") EntityManager em;
    @EJB RealmDAO realm;
    
    static final Function<Subject, User> MODEL2DTO = subject -> {
        if (subject == null) return null;
        
        User user = new User();
        user.setId(subject.getUsername());
        //don't populate hashed password here!
        
        return user;
    };
    
    static final Function<User, Subject> DTO2MODEL = dto -> {
        Subject subject = new Subject();
        subject.setUsername(dto.getId());
        subject.setPassword(dto.getPassword());
        
        return subject;
    };
    
    @Override
    public Function<Subject, User> model2dto() {
        return MODEL2DTO;
    }

    @Override
    public List<User> fetch(/**/) {
        TypedQuery<Subject> query = em.createNamedQuery(Subject.QUERY_ALL, Subject.class);
        return fetch(query);
    }

    @Override
    public List<User> fetchBy(Role role) {
        TypedQuery<Subject> query = em.createNamedQuery(Subject.QUERY_BY_ROLE, Subject.class);
        query.setParameter(PARAM_ROLE, role.getId());
        
        return fetch(query);
    }

    @Override
    public List<User> fetchBy(Permission permission) {
        TypedQuery<Subject> query = em.createNamedQuery(Subject.QUERY_BY_PERMISSION, Subject.class);
        query.setParameter(PARAM_RESOURCE, permission.getResource());
        query.setParameter(PARAM_ACTION, permission.getAction());
        
        return fetch(query);
    }
    
    @Override
    public User find(String username) {
        Subject subject = findSubject(username);
        return MODEL2DTO.apply(subject);
    }

    @Override
    public Boolean delete(String username) {
        Subject subject = findSubject(username);
        if (subject == null) return null;
        
        try {
            realm.remove(subject);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean update(String username, User user) {
        Subject subject = findSubject(username);
        if (subject == null) return null;
        
        try {
            subject.setUsername(user.getId());
            subject.setPassword(user.getPassword());
            
            realm.update(subject);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String create(User user) {
        try {
            realm.create(DTO2MODEL.apply(user));
            return user.getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean link(boolean link, User user, Role roleGroup) {
        try {
            realm.linkUserRole(link, user.getId(), roleGroup.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Grant(check = false)
    Subject findSubject(String username) {
        try {
            return realm.findSubjectBy(username);
        } catch (Exception e) {
            return null;
        }
    }
}
