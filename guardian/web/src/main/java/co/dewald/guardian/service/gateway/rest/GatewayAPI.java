package co.dewald.guardian.service.gateway.rest;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


/**
 * @author Dewald Pretorius
 */
@ApplicationPath("gateway")
public class GatewayAPI extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(GuardianResource.class);
        
        return resources;
    }
}
