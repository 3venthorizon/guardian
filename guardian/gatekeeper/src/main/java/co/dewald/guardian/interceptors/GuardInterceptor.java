package co.dewald.guardian.interceptors;


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

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import co.dewald.guardian.gate.Guard;
import co.dewald.guardian.gate.Guardian;
import co.dewald.guardian.gate.Session;
import co.dewald.guardian.gate.Grant;


/**
 * The Guard Interceptor is a runtime annotation processor that is invoked when a {@link Guard}ed resource is accessed.
 * 
 * @author Dewald Pretorius
 */
@Guard @Interceptor 
public class GuardInterceptor {
    
    @EJB(beanName = "GuardianCore") Guardian guardian;
    @Inject Session session;
    
    @AroundInvoke
    public Object grant(InvocationContext ctx) throws Exception { 
        Method method = ctx.getMethod();
        Class<?> type = method.getDeclaringClass();
        Grant resource = getResource(type);
        Grant action = getAction(resource, method);
        
        Boolean state = guardian.checkState(resource.name(), action.name());
        if (!Boolean.TRUE.equals(state)) return null;
        
        if (!action.check()) return ctx.proceed(); 
        authorise(session.getUsername(), resource.name(), action.name());
        
        filterMethodParameters(ctx, session.getUsername());
        
        Object result = relayUser(ctx, resource, action);
        if (action.filter() && result != null) result = filter(session.getUsername(), null, result);
        return result;
    }
    
    void authorise(String username, String resource, String action) {
        Boolean authorised = guardian.authorise(username, resource, action);
        
        if (!Boolean.TRUE.equals(authorised)) {
            throw new SecurityException("Unauthorised access - " + resource + ":" + action);
        }
    }
    
    Grant createGrant(final String name, final boolean check, final boolean filter, final String relayUser) {
        //@formatter:off
        Grant grant = new Grant() {
            @Override
            public Class<? extends Annotation> annotationType() { return Grant.class; }
            
            @Override
            public String name() { return name; }
            @Override
            public boolean check() { return check; }
            @Override
            public boolean filter() { return filter; }
            @Override
            public String relayUser() { return relayUser; }
        };
        //@formatter:on
        
        return grant;
    }
    
    Grant createGrant(String reflectName, Grant annotated) {
        if (!annotated.name().isEmpty()) return annotated;
        return createGrant(reflectName, annotated.check(), annotated.filter(), annotated.relayUser());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    Object filter(String username, String filter, Object object) {
        if (object instanceof Map) {
            Map map = (Map) object;
            filterCollection(username, filter, map.keySet());
            filterCollection(username, filter, map.values());
            
            return object;
        } 

        Collection<Object> collection; 
        boolean isCollection = object instanceof Collection;

        if (isCollection) collection = (Collection) object;
        else { //wrap parameter in a mutable/filter safe collection
            collection = new ArrayList<>();
            collection.add(object);
        }
        
        try {
            filterCollection(username, filter, collection);
            if (!isCollection) return collection.isEmpty() ? null : object; //filter wrapped parameter 
        } catch (UnsupportedOperationException uoe) { //immutable collection parameter
            if (collection instanceof List) collection = new LinkedList<>(collection);
            else if (collection instanceof Set) collection = new LinkedHashSet<>(collection);
            else throw uoe;
            
            filterCollection(username, filter, collection);
        }

        return collection;
    }
    
    @SuppressWarnings("unchecked")
    <T> void filterCollection(String username, String resource, Collection<T> collection) {  
        if (collection == null || collection.isEmpty()) return;
        if (resource == null) resource = getFilterResource(collection);
        if (resource == null) return; //data filter name/type undefined
        
        Map<String, T> filterMap = new LinkedHashMap<>(collection.size()); 
        List<T> recursiveElements = new ArrayList<>(collection.size()); 
        boolean substituted = false; 
        
        for (T element : collection) {
            if (element == null) filterMap.put(null, null);
            else if (element instanceof Collection || element instanceof Map) {
                Object mutableElement = filter(username, resource, element); 
                recursiveElements.add((T) mutableElement);
                substituted |= element != mutableElement;
            } else filterMap.put(element.toString(), element);
        }
        
        List<T> filtered = guardian.filter(username, resource, filterMap);
        filtered.addAll(recursiveElements);
        
        if (filtered == null || filtered.isEmpty()) collection.clear();
        else {
            collection.retainAll(filtered);
            
            if (substituted) {
                recursiveElements.removeAll(collection);
                collection.addAll(recursiveElements);
            }
        }
    }
    
    void filterMethodParameters(InvocationContext ctx, String username) {
        Object[] parameters = ctx.getParameters();
        if (parameters == null || parameters.length < 1) return;
        
        Method method = ctx.getMethod();
        boolean filtered = filterParameters(username, method, parameters);

        if (filtered) return;
        
        for (Class<?> iclass : method.getDeclaringClass().getInterfaces()) {
            try {
                Method imethod = iclass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                filtered = filterParameters(username, imethod, parameters);
                if (filtered) return;
            } catch (NoSuchMethodException e) { }
        }
    }
    
    boolean filterParameters(String username, Method method, Object[] parameters) {
        boolean filtered = false;
        Annotation[][] annotations = method.getParameterAnnotations();
        
        for (int index = 0; index < parameters.length; index++) {
            if (parameters[index] == null) continue;
            Grant grant = null;
            
            for (Annotation annotation : annotations[index]) {
                if (!(annotation instanceof Grant)) continue;
                grant = (Grant) annotation;
                break;
            }
            
            if (grant == null) continue;
            if (!grant.filter()) continue;
            
            filtered = true; //presence of @Grant means it is filtered
            String filter = null;
            if (!grant.name().isEmpty()) filter = grant.name();
                
            parameters[index] = filter(username, filter, parameters[index]);
        }
        
        return filtered;
    }
    
    Grant getAction(final Grant resource, final Method method) {
        Grant grant = method.getAnnotation(Grant.class);
        if (grant != null) return createGrant(method.getName(), grant);
        
        for (Class<?> iclass : method.getDeclaringClass().getInterfaces()) {
            try {
                Method imethod = iclass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                grant = imethod.getAnnotation(Grant.class);
                if (grant != null) return createGrant(imethod.getName(), grant);
            } catch (NoSuchMethodException e) { }
        }
        
        return createGrant(method.getName(), true, resource.filter(), ""); //inherit resource filter
    }
    
    <T> String getFilterResource(Collection<T> collection) {
        for (T element : collection) {
            if (element == null) continue;
            
            Grant grant = getResource(element.getClass());
            if (!grant.filter()) return null; 
            
            return grant.name();
        }
        
        return null;
    }

    Grant getResource(final Class<?> clazz) {
        Grant grant = clazz.getAnnotation(Grant.class);
        if (grant != null) return createGrant(clazz.getName(), grant);

        for (Class<?> iclass : clazz.getInterfaces()) {
            grant = iclass.getAnnotation(Grant.class);
            if (grant != null) return createGrant(clazz.getName(), grant);
        }

        return createGrant(clazz.getName(), true, false, "");
    }
    
    Object relayUser(InvocationContext ctx, Grant resource, Grant action) throws Exception {
        String username = session.getUsername();
        String delegate = username;
        
        try {
            if (!action.relayUser().isEmpty()) delegate = action.relayUser();
            else if (!resource.relayUser().isEmpty()) delegate = resource.relayUser();
            
            session.setUsername(delegate);
            
            return ctx.proceed();
        } finally {
            session.setUsername(username);
        }
    }
}
