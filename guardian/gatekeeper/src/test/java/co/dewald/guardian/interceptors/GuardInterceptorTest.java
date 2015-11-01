package co.dewald.guardian.interceptors;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import co.dewald.guardian.gate.Grant;
import co.dewald.guardian.gate.Guardian;
import co.dewald.guardian.gate.Session;


/**
 * 
 * @author Dewald Pretorius
 */
@RunWith(MockitoJUnitRunner.class)
public class GuardInterceptorTest {

    @Mock Guardian guardian;
    GuardInterceptor interceptor;
    Session session;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        interceptor = new GuardInterceptor();
        interceptor.guardian = guardian;
    }
    
    @Test
    public void createGrantTestNameSubstitution() {
        Grant grant = BlankGrantName.class.getAnnotation(Grant.class);
        assertNotNull(grant);
        assertTrue(grant.name().isEmpty());
        
        Grant substituted = interceptor.createGrant("ClassName", grant);
        
        assertNotNull(substituted);
        assertEquals("ClassName", substituted.name());
    }

    //@formatter:off
    @Grant
    class BlankGrantName {}
    //@formatter:on
    
    @Test
    public void getResourceTestDefaultReflection() {
        Class<?> classMock = DefaultReflection.class;
        
        Grant grant = interceptor.getResource(classMock);
        
        assertEquals(Grant.class, grant.annotationType());
        assertEquals("co.dewald.guardian.interceptors.GuardInterceptorTest$DefaultReflection", grant.name());
        assertTrue(grant.check());
        assertFalse(grant.filter());
    }
    
    class DefaultReflection implements Serializable {
        static final long serialVersionUID = -6208762122315325812L;
    }
    
    @Test
    public void getResourceTestDirectAnnotation() {
        Class<?> classMock = DirectAnnotation.class;
        
        Grant grant = interceptor.getResource(classMock);
        
        assertEquals("DirectAnnotation", grant.name());
    }
    
    //@formatter:off
    @Grant(name = "DirectAnnotation")
    class DirectAnnotation { }
    //@formatter:on

    @Test
    public void getResourceTestAnnotatedInterface() {
        Class<?> classMock = IndirectAnnotation.class;
        
        Grant grant = interceptor.getResource(classMock);
        
        assertEquals("GuardInterfaceImplementation", grant.name());
    }
    
    //@formatter:off
    @Grant(name = "GuardInterfaceImplementation")
    interface InterfaceAnnotation { }
    class IndirectAnnotation implements InterfaceAnnotation { }
    //@formatter:on
}