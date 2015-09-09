package co.dewald.guardian.realm;


import java.io.Serializable;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
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
    @NamedQuery(name = Subject.QUERY_IN_USERNAMES, query = Subject.JPQL_IN_USERNAMES),
    @NamedQuery(name = Subject.QUERY_BY_ROLE, query = Subject.JPQL_BY_ROLE) })
@Entity
@Table(name = "subject")
public class Subject extends RealmEntity implements Serializable {
    public static final String QUERY_ALL = "Subject[all]";
    public static final String JPQL_ALL = "SELECT s FROM Subject s";
    
    public static final String QUERY_IN_USERNAMES = "Subject[usernames]";
    public static final String JPQL_IN_USERNAMES = "SELECT s FROM Subject s WHERE s.username IN :usernames";
    public static final String PARAM_USERNAMES = "usernames";
    
    public static final String QUERY_BY_ROLE = "Subjects[role]";
    public static final String JPQL_BY_ROLE = "SELECT s FROM Subject s JOIN s.roles r WHERE r.group = :group";
    public static final String PARAM_ROLE = "group";
    
    static final long serialVersionUID = -1092299602072486407L;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String salt;

    @Pattern(regexp = "^[a-z0-9_-]{8,100}$")
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
     * Hash and Salts the password. The following constraints are required to ensure a minimum strength password.
     * <ul>
     * <li>digit from 0-9</li>
     * <li>lowercase character</li>
     * <li>uppercase character</li>
     * <li>special symbols ~'!@#$%?\\\/&*\]|\[=()}"{+_:;,.><'-</li>
     * <li>8 or more characters long</li>
     * </ul>
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