package co.dewald.guardian.realm;


import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;


/**
 * The persistent class for the access_log database table.
 * 
 * @author Dewald Pretorius
 */
@Entity
@Table(name = "access_log")
public class AccessLog extends RealmEntity implements Serializable {

    static final long serialVersionUID = -17102553397124431L;
    
    static final int MAX_TEXT = 100;

    @Column(updatable = false, nullable = false, length = MAX_TEXT)
    private String action;

    @Column(updatable = false, nullable = false)
    private boolean granted;

    @Column(updatable = false, length = MAX_TEXT)
    private String message;

    @Column(updatable = false, nullable = false, length = MAX_TEXT)
    private String resource;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    private Date timestamp;

    @Column(updatable = false, nullable = false, length = MAX_TEXT)
    private String username;

    public AccessLog() {
        super();
        
        setTimestamp(new Date());
        setGranted(false);
    }

    public AccessLog(Date timestamp, String username, String resource, String action, boolean granted, 
                     String message) {
        this();
        
        setAction(action);
        setGranted(granted);
        setMessage(message);
        setResource(resource);
        setTimestamp(timestamp);
        setUsername(username);
    }
    
    /**
     * Sets the message for this event and truncates the text to 100 characters.
     * 
     * @param message
     */
    public void setMessage(String message) { 
        this.message = message; 
        
        if (this.message == null) return;
        if (this.message.length() < MAX_TEXT) return;
        
        this.message = message.substring(0, MAX_TEXT);
    }
    
    //@formatter:off
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public boolean getGranted() { return granted; }
    public void setGranted(boolean granted) { this.granted = granted; }
    
    public String getMessage() { return message; }
    
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    //@formatter:on

}