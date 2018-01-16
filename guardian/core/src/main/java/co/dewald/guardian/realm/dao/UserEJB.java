package co.dewald.guardian.realm.dao;


import java.util.List;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import co.dewald.guardian.dao.DAO;
import co.dewald.guardian.dto.DTO;
import co.dewald.guardian.dto.User;
import co.dewald.guardian.gate.Grant;
import co.dewald.guardian.gate.Guard;
import co.dewald.guardian.realm.Permission;
import co.dewald.guardian.realm.Role;
import co.dewald.guardian.realm.Subject;


/**
 * @author Dewald Pretorius
 */
//FIXME @Guard
@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateless(name = "UserDAO")
public class UserEJB implements Model2DTO<Subject, User>, DAO<User> {

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
    public String getId(User id) {
        return id.getId();
    }

    @Override
    public User getId(String id) {
        User userId = new User();
        userId.setId(id);
        
        return userId;
    }

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
    public <C extends DTO> List<User> fetchBy(C criteria) {
        try {
            TypedQuery<Subject> query;
            
            if (criteria instanceof co.dewald.guardian.dto.Permission) {
                co.dewald.guardian.dto.Permission permissionCriteria = (co.dewald.guardian.dto.Permission) criteria;
                query = em.createNamedQuery(Subject.QUERY_BY_PERMISSION, Subject.class);
                query.setParameter(Permission.PARAM_RESOURCE, permissionCriteria.getResource());
                query.setParameter(Permission.PARAM_ACTION, permissionCriteria.getAction());
            } else if (criteria instanceof User) {
                query = em.createNamedQuery(Subject.QUERY_BY_ROLE, Subject.class);
                query.setParameter(Role.PARAM_ROLE, criteria.getId());
            } else return null;
            
            return fetch(query);
        } catch (Exception e) {
            return null;
        }
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
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
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
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
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
    public <R extends DTO> Boolean linkReference(boolean link, User id, R reference) {
        try {
            if (reference instanceof co.dewald.guardian.dto.Role) {
                realm.linkUserRole(link, id.getId(), reference.getId());
                return Boolean.TRUE;
            }
        } catch (NullPointerException npe) {
            return null;
        } catch (Exception e) {
        }
        
        return Boolean.FALSE;
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
