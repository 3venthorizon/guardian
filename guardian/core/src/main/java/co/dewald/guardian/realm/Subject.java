package co.dewald.guardian.realm;


import java.io.Serializable;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;


/**
 * The persistent class for the subject database table.
 * 
 * @author Dewald Pretorius
 */
@NamedQueries({
    @NamedQuery(name = Subject.QUERY_ALL, query = Subject.JPQL_ALL),
    @NamedQuery(name = Subject.QUERY_SUBJECT, query = Subject.JPQL_BY_USERNAME),
    @NamedQuery(name = Subject.QUERY_BY_ROLE, query = Subject.JPQL_BY_ROLE),
    @NamedQuery(name = Subject.QUERY_BY_PERMISSION, query = Subject.JPQL_BY_PERMISSION)})
@Entity
@Table(name = "subject")
public class Subject extends RealmEntity implements Serializable {
    public static final String PARAM_USERNAME = "username";

    public static final String QUERY_ALL = "Subject[all]";
    public static final String QUERY_SUBJECT = "Subject[username]";
    public static final String QUERY_BY_ROLE = "Subjects[role]";
    public static final String QUERY_BY_PERMISSION = "Subject[permission]";
    
    static final String WHERE_SUBJECT = "WHERE s.username = :" + PARAM_USERNAME;
    
    static final String JPQL_ALL = "SELECT s FROM Subject s";
    static final String JPQL_BY_USERNAME = "SELECT s FROM Subject s " + WHERE_SUBJECT;
    static final String JPQL_BY_ROLE = "SELECT s FROM Subject s JOIN s.roles r " + Role.WHERE_ROLE;
    static final String JPQL_BY_PERMISSION = 
        "SELECT s FROM Subject s JOIN s.roles r JOIN r.permissions p " + Permission.WHERE_PERMISSION;
    
    static final long serialVersionUID = -1092299602072486407L;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String salt;

    @Column(unique = true, updatable = false, nullable = false, length = 100)
    private String username;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(name = "subject_role_map", 
    	       joinColumns = { @JoinColumn(name = "subject_id", nullable = false) }, 
    	       inverseJoinColumns = { @JoinColumn(name = "role_id", nullable = false) })
    private Set<Role> roles;

    public Subject() {
        super();
    }
    
    public static String salt() {
        byte[] saltBytes = new byte[64];
        SecureRandom random = new SecureRandom();
        
        random.nextBytes(saltBytes);
        return DatatypeConverter.printBase64Binary(saltBytes);
    }
    
    /**
     * Matches the password supplied against the persisted password.
     * 
     * @param password
     * @return true if the password is a match
     */
    public boolean matchPassword(String password) {
        try {
            String hash = hash(password, this.salt);
            return this.password.equals(hash);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Hash and Salts the password.
     * 
     * @param password
     */
    public void setPassword(String password) { 
        try {
            setSalt();
            this.password = hash(password, salt);
        } catch (Exception e) {
            throw new RuntimeException(" Error Updating Password for Username: " + username, e);
        } 
    }

    public Set<Role> getRoles() {
        if (roles == null) roles = new HashSet<Role>();
        return roles;
    }
    
    void setSalt() {
        salt = salt();
    }
    
    String hash(String password, String salt) throws Exception {
        byte[] saltBytes = DatatypeConverter.parseBase64Binary(salt);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 58, 512);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hashBytes = skf.generateSecret(spec).getEncoded();
        String hash64 = DatatypeConverter.printBase64Binary(hashBytes);
        
        return hash64;
    }
    
    //@formatter:off
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    //@formatter:on 
}