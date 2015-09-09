package co.dewald.guardian;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import co.dewald.guardian.gate.Guard;
import co.dewald.guardian.gate.Guardian;
import co.dewald.guardian.gate.Grant;
import co.dewald.guardian.session.Session;


/**
 * The Guard Interceptor is a runtime annotation processor that is invoked when a {@link Guard}ed resource is accessed.
 * This interceptor will perform the following checks on the {@link Guard}ed context in order:
 * <ol>
 * <li>{@link Guardian#checkState(String, String)} and may throw a {@link SecurityException}</li>
 * <li>When the resource class or action method is <b>@{@link Grant#check()} false</b>, 
 * only the last step is executed.</li>
 * <li>{@link Guardian#authorise(String, String, String)} and may throw a {@link SecurityException}.</li>
 * <li>{@link Guardian#filter(String, String, java.util.Map)} parameter values where the resource or  action or 
 * parameter is @{@link Grant#filter()} true</li>
 * <li><b>Execute</b> and return resource.action(parameters...)</li>
 * <li>{@link Guardian#filter(String, String, java.util.Map)} return values where the resource or  action
 * is @{@link Grant#filter()} true</li>
 * </ol>
 * 
 * @author Dewald Pretorius
 */
@Guard @Interceptor 
public class GuardInterceptor {
    
    @Inject Guardian guardian;
    @Inject Session session;
    
    @AroundInvoke
    public Object grant(InvocationContext ctx) throws Exception { 
        Method method = ctx.getMethod();
        Class<?> type = method.getDeclaringClass();
        String resource = type.getName();
        String action = method.getName();
        Boolean state = guardian.checkState(resource, action);
        if (!Boolean.TRUE.equals(state)) return null;
        
        boolean check = true;
        boolean filter = false;
        
        Grant grantResource = type.getAnnotation(Grant.class);
        Grant grantAction = method.getAnnotation(Grant.class);
        
        if (grantResource != null) {
            if (!grantResource.name().isEmpty()) resource = grantResource.name(); 
            check &= grantResource.check();
            filter = grantResource.filter();
        }
        if (grantAction != null) {
            if (!grantAction.name().isEmpty()) action = grantAction.name(); 
            check &= grantAction.check();
            filter = grantAction.filter();
        }
        
        if (!check) return ctx.proceed(); 
        authorise(session.getUsername(), resource, action);
        if (!filter) return ctx.proceed();
        filterParameters(ctx, session.getUsername());
        return filterResult(ctx, session.getUsername());
    }
    
    void authorise(String username, String resource, String action) {
        Boolean authorised = guardian.authorise(username, resource, action);
        
        if (!Boolean.TRUE.equals(authorised)) {
            throw new SecurityException("Unauthorised access - " + resource + ":" + action);
        }
    }
    
    <T> String extractFilteredResource(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) return null;
        
        for (T element : collection) {
            if (element == null) continue;
            String resource = extractFilteredResource(element.getClass());
            if (resource != null && !resource.isEmpty()) return resource;
        }
        
        return null;
    }
    
    String extractFilteredResource(Class<?> clazz) {
        Grant grant = clazz.getAnnotation(Grant.class);
        if (grant == null) return null; //data filter name/type undefined
        if (!grant.filter()) return null; 
        return grant.name();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    Object filter(String username, String filter, Object object) {
        Object filtered = object;
        
        if (filtered instanceof Map) {
            Map map = (Map) filtered;
            filterCollection(username, filter, map.keySet());
            filterCollection(username, filter, map.values());
        } else {
            Collection<Object> collection; 
            boolean isCollection = filtered instanceof Collection;

            if (isCollection) collection = (Collection) filtered;
            else {
                collection = new ArrayList<>();
                collection.add(filtered);
            }
            
            try {
                filterCollection(username, filter, collection);
            } catch (UnsupportedOperationException uoe) {
                if (collection instanceof List) collection = new LinkedList<>(collection);
                else if (collection instanceof Set) collection = new LinkedHashSet<>(collection);
                else throw uoe;
                
                filterCollection(username, filter, collection);
            }
            
            if (!isCollection) {
                return collection.isEmpty() ? null : collection.iterator().next();
            } else filtered = collection;
        }
        
        return filtered;
    }
    
    /**
     * @param username
     * @param resource
     * @param collection
     */
    <T> void filterCollection(String username, String resource, Collection<T> collection) {  
        if (collection == null || collection.isEmpty()) return;
        if (resource == null) resource = extractFilteredResource(collection);
        if (resource == null || resource.isEmpty()) return; //data filter name/type undefined
        
        Map<String, T> tuple = new LinkedHashMap<>(collection.size()); 
        boolean recursive = false;
        
        for (T element : collection) {
            if (element == null) tuple.put(null, element);
            else if (element instanceof Collection || element instanceof Map) {
                recursive = true;
                filter(username, resource, element);
            } else tuple.put(element.toString(), element);
        }
        
        if (recursive) return;
        
        List<T> filtered = guardian.filter(username, resource, tuple);
        
        if (filtered == null) collection.clear();
        else if (filtered.isEmpty()) collection.clear();
        else collection.retainAll(filtered);
    }
    
    void filterParameters(InvocationContext ctx, String username) {
        Object[] parameters = ctx.getParameters();
        if (parameters == null || parameters.length < 1) return;
        Annotation[][] annotations = ctx.getMethod().getParameterAnnotations();
        
        for (int index = 0; index < parameters.length; index++) {
            if (parameters[index] == null) continue;
            String filter = null;
            Grant grantParameter = null;
            
            for (Annotation annotation : annotations[index]) {
                if (!(annotation instanceof Grant)) continue;
                grantParameter = (Grant) annotation;
                break;
            }
            
            if (grantParameter != null) {
                if (grantParameter.name().isEmpty()) {
                    if (!grantParameter.filter()) continue;
                } else filter = grantParameter.name();
            }
            
            parameters[index] = filter(username, filter, parameters[index]);
        }
    }
    
    Object filterResult(InvocationContext ctx, String username) throws Exception {
        Object result = ctx.proceed();
        if (result == null) return result;
        
        return filter(username, null, result);
    }
}
