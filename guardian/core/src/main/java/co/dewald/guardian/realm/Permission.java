package co.dewald.guardian.realm;


import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the permission database table.
 * 
 * @author Dewald Pretorius
 */
@NamedQueries({
    @NamedQuery(name = Permission.QUERY_ALL, query = Permission.JPQL_ALL),
    @NamedQuery(name = Permission.QUERY, query = Permission.JPQL),
    @NamedQuery(name = Permission.QUERY_BY_RESOURCE, query = Permission.JPQL_BY_RESOURCE) })
@Entity
@Table(name = "permission")
public class Permission extends RealmEntity implements Serializable {
    public static final String ALL = "*";

    public static final String QUERY_ALL = "Permission[all]";
    public static final String JPQL_ALL = "SELECT p FROM Permission p";
    
    public static final String QUERY_BY_RESOURCE = "Permission[resource]";
    public static final String QUERY = "Permission[resource,action]";
    public static final String JPQL_BY_RESOURCE = 
        "SELECT p FROM Permission p " +
        "WHERE p.resource = :resource " +
        "ORDER BY p.resource";
    public static final String JPQL = 
        "SELECT p FROM Permission p " +
        "WHERE p.resource = :resource AND p.action = :action";
    public static final String PARAM_RESOURCE = "resource";
    public static final String PARAM_ACTION = "action";
    
    static final long serialVersionUID = 7254517195691039561L;

    @Column(nullable = false, length = 100)
    private String resource;
    
    @Column(nullable = false, length = 100)
    private String action;
    
    @Embedded
    private State state;

    public Permission() {
        super();
        setState(new State());
    }
    
    /**
     * @param resource
     * @param action
     * @param active
     * @param bypass
     */
    public Permission(String resource, String action, Boolean active, Boolean bypass) {
        this();
        setAction(action);
        setResource(resource);
        setActive(active);
        setBypass(bypass);
    }
    
    /**
     * Evaluates the other permission for semantic equivalence.
     * 
     * @return true if the other permission is equivalent to this
     */
    public boolean equivalent(Permission other) {
        if (other == null) return false;
        if (getId() != null && other.getId() != null && equals(other)) return true;
        if (!getResource().equals(other.getResource())) return false;
        if (ALL.equals(getAction())) return true;
        if (ALL.equals(other.getAction())) return true;
        return getAction().equals(other.getAction());
    }
    
    /**
     * Evaluates for semantic equivalence.
     * 
     * @param resource
     * @param action
     * @return true if this permission is equivalent
     */
    public boolean equivalent(String resource, String action) {
        if (!getResource().equals(resource)) return false;
        if (ALL.equals(getAction())) return true;
        if (ALL.equals(action)) return true;
        return getAction().equals(action);
    }
    
    public Boolean getActive() {
        return getState().getActive();
    }
    
    public void setActive(Boolean active) {
        getState().setActive(active);
    }
    
    public Boolean getBypass() {
        return getState().getBypass();
    }
    
    public void setBypass(Boolean bypass) {
        getState().setBypass(bypass);
    }
    
    //@formatter:off
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    //@formatter:on
}