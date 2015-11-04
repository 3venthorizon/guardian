package co.dewald.guardian.decorators;


import java.util.Date;
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
    static final String BYPASSED = "Bypassed";

    @Inject @Delegate Guardian delegate;
    @EJB RealmDAO realm;
    
    @Override
    public Boolean authenticate(String username, String password) {
        Date timestamp = new Date();
        boolean granted = false;
        String message = null;
        
        try {
            Boolean authenticated = delegate.authenticate(username, password);
            
            if (authenticated == null) message = ERROR_NOSUBJECT;
            else granted = authenticated.booleanValue();
            
            return authenticated;
        } catch(Throwable throwable) {
            message = throwable.getMessage();
            throw throwable;
        } finally {
            logEvent(timestamp, username, RESOURCE, ACTION_AUTHENTICATE, granted, message);
        }
    }

    @Override
    public Boolean authorise(String username, String resource, String action) {
        Date timestamp = new Date();
        boolean granted = false;
        String message = null;
        
        try {
            Boolean authorised = delegate.authorise(username, resource, action);
            
            if (authorised == null) message = ERROR;
            else granted = authorised.booleanValue();
            
            return authorised;
        } catch(Throwable throwable) {
            message = throwable.getMessage();
            throw throwable;
        } finally {
            logEvent(timestamp, username, resource, action, granted, message);
        }
    }
    
    @Override
    public <T> List<T> filter(String username, String resource, Map<String, T> data) {
        Date timestamp = new Date();
        boolean granted = false;
        String message = null;
        
        try {
            List<T> filtered = delegate.filter(username, resource, data);
            
            if (filtered == null || data == null) {
                message = data == null ? ERROR_NULL_FILTER : ERROR;
            } else if (filtered.size() == data.size()) granted = true;
            else message = data.size() + ERROR_FILTERED + filtered.size();
            
            return filtered;
        } catch(Throwable throwable) {
            message = throwable.getMessage();
            throw throwable;
        } finally {
            logEvent(timestamp, username, resource, ACTION_FILTER, granted, message);
        }
    }
    
    @Override
    public Boolean checkState(String resource, String action) throws SecurityException {
        try {
            Boolean state = delegate.checkState(resource, action);
            if (!Boolean.TRUE.equals(state)) logEvent(new Date(), RESOURCE, resource, action, false, BYPASSED);
            
            return state;
        } catch (SecurityException e) {
            logEvent(new Date(), RESOURCE, resource, action, false, e.getMessage());
            throw e;
        }
    }
    
    void logEvent(Date timestamp, String username, String resource, String action, boolean granted, String message) {
        AccessLog event = new AccessLog(timestamp, username, resource, action, granted, message);
        realm.create(event);
    }
}
