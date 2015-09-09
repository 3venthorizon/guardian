package co.dewald.guardian.gate;


import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;


/**
 * The Guardian defines the following functions:
 * <ul>
 * <li><b>Authenticate</b> a subject's credentials which verifies the interacting entity as registered</li>
 * <li><b>Authorise</b> or {@link Grant} an authenticated subject to access a {@link Guard}ed resource to perform an
 * action</li>
 * <li><b>Filter</b> [request parameters] / [return values] data prior to and post execution of an action respectively
 * </li>
 * <li>Indicate whether a permission(resource~action) is <b>Activated/Disabled</b></li>
 * </ul>
 * 
 * @author Dewald Pretorius
 */
public interface Guardian {
    
    /**
     * <b>Authenticate</b> a subject's credentials which verifies the interacting entity as registered 
     * 
     * @param username of subject
     * @param password not hashed
     * @return authenticated or null if the username does not exist or null when the session is authenticated.
     */
    Boolean authenticate(@NotNull String username, @NotNull String password);

    /**
     * <b>Authorise</b> or {@link Grant} an authenticated subject to access a {@link Guard}ed resource to perform an
     * action
     * 
     * @param username
     * @param resource
     * @param action that will be performed against the resource
     * @return success or null when the subject's session is expired/unauthenticated or the permission does not exist.
     */
    Boolean authorise(@NotNull String username, @NotNull String resource, @NotNull String action);
    
    /**
     * <b>Filter</b> [request parameters] / [return values] data prior to and post execution of an action respectively
     * 
     * @param username
     * @param resource
     * @param data
     * @return filtered value subset of the actionData
     */
    <T> List<T> filter(@NotNull String username, @NotNull String resource, Map<String, T> data);
    
    /**
     * Returns the token associated with this username's session.
     * 
     * @param username
     * @return session token
     */
    String getSessionToken(@NotNull String username);
    
    /**
     * Checks the state for the resource:
     * 
     * <ol>
     * <li>TRUE - when the resource is active</li>
     * <li>FALSE - when the resource is disabled and bypassed with a No-Operation</li>
     * <li>{@link SecurityException} when the resource is disabled and not bypassed</li>
     * </ol>
     * 
     * @param resource
     * @param action
     * @return state
     */
    Boolean checkState(@NotNull String resource, @NotNull String action) throws SecurityException;
    
}
