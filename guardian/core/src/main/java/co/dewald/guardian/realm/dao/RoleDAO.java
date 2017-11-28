package co.dewald.guardian.realm.dao;


import javax.ejb.EJB;
import javax.ejb.Stateless;

import co.dewald.guardian.gate.AdminResource;
import co.dewald.guardian.gate.Guard;
import co.dewald.guardian.realm.Role;


/**
 * @author Dewald Pretorius
 */
@Guard
@Stateless(name = "RoleDAO")
public class RoleDAO implements AdminResource<co.dewald.guardian.dto.Role, String> {

    @EJB RealmDAO realm;

    @Override
    public co.dewald.guardian.dto.Role find(String uniqueKey) {
        Role role = realm.findRoleBy(uniqueKey.toString());
        co.dewald.guardian.dto.Role dto = new co.dewald.guardian.dto.Role();
        
        dto.setGroup(role.getGroup());
        return dto;
    }

    @Override
    public void create(co.dewald.guardian.dto.Role resource) {
        Role role = new Role();
        role.setGroup(resource.getGroup());
        
        realm.create(role);
    }

    @Override
    public void update(co.dewald.guardian.dto.Role resource) {
        Role role = realm.findRoleBy(resource.getGroup());
        realm.update(role);
    }

    @Override
    public void delete(co.dewald.guardian.dto.Role resource) {
        Role role = realm.findRoleBy(resource.getGroup());
        realm.remove(role);
    }
}
