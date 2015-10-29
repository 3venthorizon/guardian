package co.dewald.guardian.gate;


import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;


/**
 * @author Dewald Pretorius
 */
@ApplicationScoped
public class Registry {

    Map<String, Session> registry; 
    
    public Registry() {
        registry = Collections.synchronizedSortedMap(new TreeMap<String, Session>());
    }

    public void register(Session session) {
        String token = session.getToken();
        registry.put(token, session);
    }
    
    public Session getSession(String token) {
        return registry.get(token);
    }
    
    public void expire(String token) {
        registry.remove(token);
    }
}
