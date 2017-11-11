package co.dewald.guardian.rest.administration;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


/**
 * @author Dewald Pretorius
 */
@ApplicationPath("administration")
public class AdminAPI extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(AdminResource.class);
        
        return resources;
    }
}
