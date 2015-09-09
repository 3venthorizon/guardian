package co.dewald.guardian.auditor;


import java.util.List;
import java.util.Map;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.ejb.EJB;
import javax.inject.Inject;

import co.dewald.guardian.gate.Guardian;
import co.dewald.guardian.realm.AccessLog;
import co.dewald.guardian.realm.dao.RealmDAO;


/**
 * Audit decorates the {@link Guardian} to log security events non-repudiation.
 * 
 * @author Dewald Pretorius
 */
@Decorator
public abstract class GuardAuditor implements Guardian {
    
    static final String RESOURCE = "Guardian";
    static final String ERROR = "Realm does not contain subject or permission";
    static final String ERROR_NOSUBJECT = "No subject for credentials";
    static final String ERROR_FILTERED = "== Elements : Filtered ==";
    static final String ERROR_NULL_FILTER = "Filter Null";
    static final String ACTION_AUTHENTICATE = "authenticate";
    static final String ACTION_FILTER = "filter";

    @Inject @Delegate Guardian delegate;
    @EJB RealmDAO realm;
    
    @Override
    public Boolean authenticate(String username, String password) {
        AccessLog event = new AccessLog();
        
        try {
            event.setResource(RESOURCE);
            event.setAction(ACTION_AUTHENTICATE);
            event.setUsername(username);

            Boolean authenticated = delegate.authenticate(username, password);
            
            if (authenticated == null) event.setMessage(ERROR_NOSUBJECT);
            else event.setGranted(authenticated);
            
            return authenticated;
        } catch(Throwable throwable) {
            event.setMessage(throwable.getMessage());
            throw throwable;
        } finally {
            realm.create(event);
        }
    }

    @Override
    public Boolean authorise(String username, String resource, String action) {
        AccessLog event = new AccessLog();
        
        try {
            event.setResource(resource);
            event.setAction(action);
            event.setUsername(username);

            Boolean authorised = delegate.authorise(username, resource, action);
            
            if (authorised == null) event.setMessage(ERROR);
            else event.setGranted(authorised);
            
            return authorised;
        } catch(Throwable throwable) {
            event.setMessage(throwable.getMessage());
            throw throwable;
        } finally {
            realm.create(event);
        }
    }
    
    @Override
    public <T> List<T> filter(String username, String resource, Map<String, T> data) {
        AccessLog event = new AccessLog();
        
        try {
            event.setResource(resource);
            event.setAction(ACTION_FILTER);
            event.setUsername(username);
            
            List<T> filtered = delegate.filter(username, resource, data);
            
            if (filtered == null || data == null) {
                event.setMessage(data == null ? ERROR_NULL_FILTER : ERROR);
            } else if (filtered.size() == data.size()) event.setGranted(true);
            else event.setMessage(data.size() + ERROR_FILTERED + filtered.size());
            
            return filtered;
        } catch(Throwable throwable) {
            event.setMessage(throwable.getMessage());
            throw throwable;
        } finally {
            realm.create(event);
        }
    }
}
