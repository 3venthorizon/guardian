package co.dewald.guardian.decorators;


import java.util.List;
import java.util.Map;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import co.dewald.guardian.gate.Guardian;
import co.dewald.guardian.gate.Registry;
import co.dewald.guardian.gate.Session;


/**
 * A Guardian decorator to manage sessions.
 * 
 * @author Dewald Pretorius
 */
@Decorator
public abstract class GuardShift implements Guardian {
    static final String ERROR_UNAUTHENTICATED = "Unauthenticated session for subject: ";
    static final String ERROR_MISMATCH = "Session User mismatch Subject";
    
    @Inject @Delegate Guardian delegate;
    @Inject Registry registry;
    @Inject Session session;
    
    @Override
    public Boolean authenticate(String username, String password) {
        session.setUsername(username);
        
        Boolean authenticated = delegate.authenticate(username, password);
        
        if (Boolean.TRUE.equals(authenticated)) {
            session.setLogin(session.getTouched());
            registry.register(session);
        }
        
        return authenticated;
    }
    
    @Override
    public Boolean authorise(String username, String resource, String action) {
        validate(username);
        return delegate.authorise(username, resource, action);
    }
    
    @Override
    public <T> List<T> filter(String username, String resource, Map<String, T> data) {
        validate(username);
        return delegate.filter(username, resource, data);
    }
    
    @Override
    public String getSessionToken(String username) {
        if (session.getUsername().equals(username)) return session.getToken();
        return null;
    }
    
    void validate(String username) {
        if (session.getLogin() == null) throw new SecurityException(ERROR_UNAUTHENTICATED + username);
        if (!username.equals(session.getUsername())) throw new SecurityException(ERROR_MISMATCH + username);
    }
}
