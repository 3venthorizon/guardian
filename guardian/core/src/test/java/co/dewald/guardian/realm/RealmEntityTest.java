package co.dewald.guardian.realm;


import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Dewald Pretorius
 */
public class RealmEntityTest {
    
    class TestEntity extends RealmEntity implements Serializable {
        static final long serialVersionUID = 9876543210L; 
    }
    
    class Unserializable extends RealmEntity {}

    RealmEntity realmEntity;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        realmEntity = new TestEntity();
        realmEntity.id = new Long(007L);
    }

    @Test
    public void nullIdHashCode() {
        realmEntity.id = null;
        assertNull(realmEntity.getId());
        
        int hashCode = realmEntity.hashCode();
        
        assertNotEquals(hashCode, ((int) TestEntity.serialVersionUID));
    }
    
    @Test
    public void serializableHashCode() {
        int hashCode = realmEntity.hashCode();
        
        assertNotNull(realmEntity.getId());
        assertEquals(((int) (TestEntity.serialVersionUID + realmEntity.getId())), hashCode);
    }
    
    @Test
    public void unserializableHashCode() {
        RealmEntity realmEntity = new Unserializable();
        realmEntity.id = new Long(007L);
        
        int hashCode = realmEntity.hashCode();
        
        assertNotNull(realmEntity.getId());
        assertEquals(((int) (Unserializable.class.hashCode() + realmEntity.getId())), hashCode);
    }
    
    @Test
    public void samePointer() {
        boolean equals = realmEntity.equals(realmEntity);
        
        assertTrue(equals);
    }
    
    @Test
    public void nullPointer() {
        boolean equals = realmEntity.equals(null);
        
        assertFalse(equals);
    }
    
    @Test
    public void notInstanceOf() {
        boolean equals = realmEntity.equals(realmEntity.toString());
        
        assertFalse(equals);
    }

    @Test
    public void thisNullId() {
        RealmEntity realmEntity = new TestEntity();
        
        boolean equals = realmEntity.equals(this.realmEntity);
        
        assertFalse(equals);
    }
    
    @Test
    public void otherNullId() {
        RealmEntity realmEntity = new TestEntity();
        
        boolean equals = this.realmEntity.equals(realmEntity);
        
        assertFalse(equals);
    }
    
    @Test
    public void nullIds() {
        RealmEntity left = new TestEntity();
        RealmEntity right = new TestEntity();
        
        boolean equals = left.equals(right);
        
        assertFalse(equals);
    }
    
    @Test
    public void equalIds() {
        RealmEntity realmEntity = new TestEntity();
        realmEntity.id = new Long(007L);
        
        boolean equals = this.realmEntity.equals(realmEntity);
        
        assertTrue(equals);
    }
}
