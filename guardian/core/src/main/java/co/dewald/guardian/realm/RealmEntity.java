package co.dewald.guardian.realm;


import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


/**
 * @author Dewald Pretorius
 */
@MappedSuperclass
public abstract class RealmEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, unique = true, nullable = false)
    protected Long id;
    
    //@formatter:off
    public Long getId() { return this.id; }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if (!(object instanceof RealmEntity)) return false;
        
        RealmEntity other = (RealmEntity) object;
        
        if (getId() != null) return getId().equals(other.getId());
        return false; 
    }
    
    @Override
    public int hashCode() {
        int hashCode = (int) reflectSerialVersionUID();
        if (getId() != null) return hashCode + getId().hashCode();
        return super.hashCode();
    }
    
    long reflectSerialVersionUID() {
        long serialVersionUID = 0L;
        
        try {
            Field svuid = this.getClass().getDeclaredField("serialVersionUID");
            serialVersionUID = svuid.getLong(this);
        } catch (Exception e) {
            serialVersionUID = this.getClass().hashCode();
        }
        
        return serialVersionUID;
    }
}
