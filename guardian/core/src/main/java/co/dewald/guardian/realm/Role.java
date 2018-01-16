package co.dewald.guardian.realm;


import java.io.Serializable;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;


/**
 * The persistent class for the role database table.
 * 
 * @author Dewald Pretorius
 */
@NamedQueries({ 
    @NamedQuery(name = Role.QUERY_ALL, query = Role.JPQL_ALL),
    @NamedQuery(name = Role.QUERY_ROLE, query = Role.JPQL_BY_ROLE),
    @NamedQuery(name = Role.QUERY_BY_SUBJECT, query = Role.JPQL_BY_SUBJECT),
    @NamedQuery(name = Role.QUERY_BY_PERMISSION, query = Role.JPQL_BY_PERMISSION) })
@Entity
@Table(name = "role")
public class Role extends RealmEntity implements Serializable {
    public static final String PARAM_ROLE = "role";

    public static final String QUERY_ALL = "Role[all]";
    public static final String QUERY_ROLE = "Role[role]";
    public static final String QUERY_BY_SUBJECT = "Role[subject]";
    public static final String QUERY_BY_PERMISSION = "Role[resource,action]";

    static final String WHERE_ROLE = "WHERE r.role = :" + PARAM_ROLE;
    
    static final String JPQL_ALL = "SELECT r FROM Role r";
    static final String JPQL_BY_ROLE = "SELECT r FROM Role r " + WHERE_ROLE;
    static final String JPQL_BY_SUBJECT = "SELECT r FROM Subject s JOIN s.roles r " + Subject.WHERE_SUBJECT;
    static final String JPQL_BY_PERMISSION = "SELECT r FROM Role r JOIN r.permissions p " + Permission.WHERE_PERMISSION;
    
    static final long serialVersionUID = 7943483115626295591L;

    @Column(nullable = false, unique = true, length = 100)
    private String role;

    // uni-directional many-to-many association to Permission
    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(name = "role_permission_map", 
               joinColumns = { @JoinColumn(name = "role_id", nullable = false) }, 
               inverseJoinColumns = { @JoinColumn(name = "permission_id", nullable = false) })
    private Set<Permission> permissions;

    public Role() {
        super();
    }
    
    public Role(String group) {
        this();
        
        setRole(group);
    }

    public Set<Permission> getPermissions() {
        if (permissions == null) permissions = new HashSet<Permission>();
        return permissions;
    }

    //@formatter:off
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}