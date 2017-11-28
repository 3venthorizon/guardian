package co.dewald.guardian.realm.dao;


import javax.ejb.EJB;
import javax.ejb.Stateless;

import co.dewald.guardian.dto.User;
import co.dewald.guardian.gate.AdminResource;
import co.dewald.guardian.gate.Guard;
import co.dewald.guardian.realm.Subject;


/**
 * @author Dewald Pretorius
 */
@Guard
@Stateless(name = "UserDAO")
public class UserDAO implements AdminResource<User, String> {

    @EJB RealmDAO realm;

    @Override
    public User find(String uniqueKey) {
        Subject subject = realm.findSubjectBy(uniqueKey.toString());
        User dto = new User();
        
        dto.setUsername(subject.getUsername());
        return dto;
    }

    @Override
    public void create(User resource) {
        Subject subject = new Subject();
        subject.setUsername(resource.getUsername());
        subject.setPassword(resource.getPassword());
        
        realm.create(subject);
    }

    @Override
    public void update(User resource) {
        Subject subject = realm.findSubjectBy(resource.getUsername());
        subject.setPassword(resource.getPassword());
        
        realm.update(subject);
    }

    @Override
    public void delete(User resource) {
        Subject subject = realm.findSubjectBy(resource.getUsername());
        realm.remove(subject);
    }
}
