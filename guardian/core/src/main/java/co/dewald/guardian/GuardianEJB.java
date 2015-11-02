package co.dewald.guardian;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import co.dewald.guardian.gate.Guardian;
import co.dewald.guardian.realm.Permission;
import co.dewald.guardian.realm.Role;
import co.dewald.guardian.realm.Subject;
import co.dewald.guardian.realm.dao.RealmDAO;


/**
 * Authentication and Authorisation EJB Realm.
 * 
 * @author Dewald Pretorius
 */
@Stateless(name = "GuardianCore")
public class GuardianEJB implements Guardian {
    
    @EJB RealmDAO realm;
    
    /**
     * @see GuardianEJB#authenticate(String, String)
     */
    @Override
    public Boolean authenticate(@NotNull String username, @NotNull String password) {
        try {
            Subject subject = realm.findSubjectBy(username);
            Boolean granted = subject.matchPassword(password);
            
            return granted;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @see Guardian#authorise(String, String, String, String)
     */
    @Override
    public Boolean authorise(@NotNull String username, @NotNull String resource, @NotNull String action) {
        try {
            Subject subject = realm.findSubjectBy(username);
            Permission grant = realm.findPermissionBy(resource, action);
            final Date now = new Date();
            if (!Boolean.TRUE.equals(grant.getActive())) return Boolean.FALSE;
            
            for (Role role : subject.getRoles()) {
                if (role.getPeriod() != null && !role.getPeriod().in(now)) continue;
                
                for (Permission permission : role.getPermissions()) {
                    if (permission.equivalent(grant)) return Boolean.TRUE;
                }
            }
            
            return Boolean.FALSE;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public <T> List<T> filter(String username, String resource, Map<String, T> data) {
        if (data == null) return null;
        List<String> remove = new ArrayList<>(data.keySet());
        List<T> retain = new ArrayList<>();
        if (data.isEmpty()) return retain;
        
        try {
            Subject subject = realm.findSubjectBy(username);
            final Date now = new Date();
            
            ROLE: for (Role role : subject.getRoles()) {
                if (role.getPeriod() != null && !role.getPeriod().in(now)) continue;
                
                PERMISSION: for (Permission permission : role.getPermissions()) {
                    if (remove.isEmpty()) break ROLE;
                    if (!Boolean.TRUE.equals(permission.getActive())) continue;
                    if (!permission.getResource().equals(resource)) continue;
                    
                    if (Permission.ALL.equals(permission.getAction())) {
                        remove.clear();
                        break ROLE;
                    }
                    
                    Iterator<String> iterator = remove.iterator();
                    
                    while (iterator.hasNext()) {
                        if (!permission.equivalent(resource, iterator.next())) continue;
                        
                        iterator.remove();
                        continue PERMISSION;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        
        DATA: for (Map.Entry<String, T> entry : data.entrySet()) {
            if (entry.getKey() != null && !remove.isEmpty()) {
                Iterator<String> iterator = remove.iterator();
            
                while (iterator.hasNext()) {
                    if (entry.getKey().equals(iterator.next())) {
                        iterator.remove();
                        continue DATA;
                    }
                }
            }
            
            retain.add(entry.getValue());
        }
        
        return retain;
    }

    @Override
    public Boolean checkState(@NotNull String resource, @NotNull String action) throws SecurityException {
        try {
            Permission permission = realm.loadPermission(resource, action);
           
            if (Boolean.TRUE.equals(permission.getActive())) return Boolean.TRUE;
            if (Boolean.TRUE.equals(permission.getBypass())) return Boolean.FALSE;
            throw new SecurityException("Resource Deactivated: " + resource + "#" + action);
        } catch (Exception e) {
            throw new SecurityException(e);
        }
    }
    
    @Override
    public String getSessionToken(String username) {
        return null;
    }
}
