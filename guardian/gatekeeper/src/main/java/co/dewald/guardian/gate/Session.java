package co.dewald.guardian.gate;


import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.xml.bind.DatatypeConverter;


/**
 * @author Dewald Pretorius
 */
@SessionScoped
public class Session implements Serializable {
    
    public static final String GUEST = "guest";
    
    static final long serialVersionUID = -6672192447261338120L;
    
    String username;
    String token;
    Date touched;
    Date login;

    public Session() {
        touched = new Date();
        token = Session.salt();
        username = GUEST;
    }
    
    static String salt() {
        byte[] saltBytes = new byte[64];
        SecureRandom random = new SecureRandom();
        
        random.nextBytes(saltBytes);
        return DatatypeConverter.printBase64Binary(saltBytes);
    }
    
    public void merge(Session that) {
        this.username = that.username;
        this.token = that.token;
        this.touched = that.touched;
        this.login = that.login;
    }

    public String getUsername() { 
        touched.setTime(System.currentTimeMillis());
        return username; 
    }
    
    public void setUsername(String username) { 
        touched.setTime(System.currentTimeMillis());
        this.username = username; 
    }

    public String getToken() { 
        touched.setTime(System.currentTimeMillis());
        return token; 
    }
    
    public void setToken(String token) { 
        touched.setTime(System.currentTimeMillis());
        this.token = token; 
    }

    public Date getLogin() { 
        touched.setTime(System.currentTimeMillis());
        return login;
    }
    
    public void setLogin(Date login) { 
        touched.setTime(System.currentTimeMillis());
        
        if (login != null) {
            if (this.login == null) this.login = new Date(login.getTime());
            else this.login.setTime(login.getTime());
        } else this.login = null;
    }
    
    //@formatter:off
    public Date getTouched() { return touched; }
    //@formatter:on
}
