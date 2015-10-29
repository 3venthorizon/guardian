package co.dewald.guardian.interceptors;


import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import co.dewald.guardian.gate.Gate;
import co.dewald.guardian.gate.Registry;
import co.dewald.guardian.gate.Session;
import co.dewald.guardian.gate.Token;


/**
 * @author Dewald Pretorius
 */
@Gate @Interceptor
public class GateKeeper {
    
    @Inject Registry registry;
    @Inject Session session;
    
    @AroundInvoke
    public Object open(InvocationContext ctx) throws Exception {
        Object[] parameters = ctx.getParameters();
        if (parameters == null || parameters.length < 1) return ctx.proceed();
        Annotation[][] annotations = ctx.getMethod().getParameterAnnotations();
        
        for (int index = 0; index < parameters.length; index++) {
            if (parameters[index] == null) continue;
            
            for (Annotation annotation : annotations[index]) {
                if (!(annotation instanceof Token)) continue;
                
                String token = parameters[index].toString();
                Session registered = registry.getSession(token);
                
                if (registered == null) return ctx.proceed();
                
                session.merge(registered);
                registry.register(session); //swap out the session pointer
                
                return ctx.proceed();
            }
        }

        return ctx.proceed();
    }
}
