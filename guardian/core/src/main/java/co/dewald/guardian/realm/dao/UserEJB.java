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
        User user = new User();
        user.setUsername(subject.getUsername());
        
        return user;
    };
    
    static final Function<User, Subject> DTO2MODEL = dto -> {
        Subject subject = new Subject();
        subject.setUsername(dto.getUsername());
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
        query.setParameter(PARAM_ROLE, role.getGroup());
        
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
        Subject subject = realm.findSubjectBy(username);
        User dto = MODEL2DTO.apply(subject);
        
        return dto;
    }

    @Override
    public void delete(String username) {
        Subject subject = realm.findSubjectBy(username);
        realm.remove(subject);
    }

    @Override
    public void update(String username, User user) {
        Subject subject = realm.findSubjectBy(user.getUsername());
        subject.setUsername(user.getUsername());
        subject.setPassword(user.getPassword());
        
        realm.update(subject);
    }

    @Override
    public void create(User user) {
        realm.create(DTO2MODEL.apply(user));
    }

    @Override
    public void link(boolean link, User user, Role roleGroup) {
        realm.linkUserRole(link, user.getUsername(), roleGroup.getGroup());
    }
}
