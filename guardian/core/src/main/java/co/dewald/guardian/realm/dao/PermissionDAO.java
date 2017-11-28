package co.dewald.guardian.realm.dao;


import javax.ejb.EJB;
import javax.ejb.Stateless;

import co.dewald.guardian.gate.AdminResource;
import co.dewald.guardian.gate.Guard;
import co.dewald.guardian.realm.Permission;


/**
 * @author Dewald Pretorius
 */
@Guard
@Stateless(name = "PermissionDAO")
public class PermissionDAO 
    implements AdminResource<co.dewald.guardian.dto.Permission, co.dewald.guardian.dto.Permission> {

    @EJB RealmDAO realm;

    @Override
    public co.dewald.guardian.dto.Permission find(co.dewald.guardian.dto.Permission uniqueKey) {
        Permission permission = realm.findPermissionBy(uniqueKey.getResource(), uniqueKey.getAction());
        co.dewald.guardian.dto.Permission dto = new co.dewald.guardian.dto.Permission();
        
        dto.setResource(permission.getResource());
        dto.setAction(permission.getAction());
        dto.setActive(permission.getActive());
        dto.setBypass(permission.getBypass());
        
        return dto;
    }

    @Override
    public void create(co.dewald.guardian.dto.Permission resource) {
        Permission permission = new Permission();
        permission.setResource(resource.getResource());
        permission.setAction(resource.getAction());
        permission.setActive(resource.isActive());
        permission.setBypass(resource.isBypass());
        
        realm.create(permission);
    }

    @Override
    public void update(co.dewald.guardian.dto.Permission resource) {
        Permission permission = realm.findPermissionBy(resource.getResource(), resource.getAction());
        permission.setActive(resource.isActive());
        permission.setBypass(resource.isBypass());
        
        realm.update(permission);
    }

    @Override
    public void delete(co.dewald.guardian.dto.Permission resource) {
        Permission permission = realm.findPermissionBy(resource.getResource(), resource.getAction());
        realm.remove(permission);
    }
}
