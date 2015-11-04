package co.dewald.guardian.interceptors;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    
    //@formatter:off
    
    @Grant
    class BlankGrantName {}

    @Grant(filter = true)
    class DefaultReflectionFilter {
        @SuppressWarnings("unused")
        private String inheritFilter() { return "Implicit Method Filter Inherited"; }
    }

    class DefaultReflection implements Serializable {
        static final long serialVersionUID = -6208762122315325812L;
    }

    @Grant(name = "DirectAnnotation")
    class DirectAnnotation {
        @Grant(check = false, filter = false)
        void action() { }
    }

    @Grant(name = "GuardInterfaceImplementation", filter = true)
    interface InterfaceAnnotation {
        @Grant(check = false)
        String polymorphism();
    }

    interface InterfaceCollision {
        String polymorphism();
    }

    class IndirectAnnotation implements Cloneable, Serializable, InterfaceCollision, InterfaceAnnotation {
        static final long serialVersionUID = -5201589352513726415L;
    
        @Override
        public String polymorphism() {
            return "covered";
        } 
    }
    
    //@formatter:on

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

    @Test
    public void getActionTestDefaultReflectionFilterInheritance() throws Exception {
        Class<?> classMock = DefaultReflectionFilter.class;
        Method method = classMock.getDeclaredMethod("inheritFilter", new Class<?>[0]);
        Grant resource = interceptor.getResource(classMock);
        
        Grant grant = interceptor.getAction(resource, method);
        
        assertEquals("inheritFilter", grant.name());
        assertTrue(grant.filter());
        assertTrue(grant.check());
        
    }
    
    @Test
    public void getResourceTestDefaultReflection() {
        Class<?> classMock = DefaultReflection.class;
        
        Grant grant = interceptor.getResource(classMock);
        
        assertEquals(Grant.class, grant.annotationType());
        assertEquals("co.dewald.guardian.interceptors.GuardInterceptorTest$DefaultReflection", grant.name());
        assertTrue(grant.check());
        assertFalse(grant.filter());
    }
    
    @Test
    public void getActionTestDirectAnnotation() throws Exception {
        Class<?> classMock = DirectAnnotation.class;
        Method method = classMock.getDeclaredMethod("action", new Class<?>[0]);
        Grant resource = interceptor.getResource(classMock);
        
        Grant grant = interceptor.getAction(resource, method);
        
        assertEquals("action", grant.name());
        assertFalse(grant.check());
        assertFalse(grant.filter());
    }
    
    @Test
    public void getResourceTestDirectAnnotation() {
        Class<?> classMock = DirectAnnotation.class;
        
        Grant grant = interceptor.getResource(classMock);
        
        assertEquals("DirectAnnotation", grant.name());
    }
    
    @Test
    public void getActionTestAnnotatedInterface() throws Exception {
        Class<?> classMock = IndirectAnnotation.class;
        Method method = classMock.getDeclaredMethod("polymorphism", new Class<?>[0]);
        Grant resource = interceptor.getResource(classMock);
        
        Grant grant = interceptor.getAction(resource, method);
        
        assertEquals("polymorphism", grant.name());
        assertFalse(grant.check());
    }

    @Test
    public void getResourceTestAnnotatedInterface() {
        Class<?> classMock = IndirectAnnotation.class;

        Grant grant = interceptor.getResource(classMock);
        
        assertEquals("GuardInterfaceImplementation", grant.name());
        assertTrue(grant.filter());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void getFilterResourceTestEmptyCollection() {
        String filterResource = interceptor.getFilterResource(Collections.EMPTY_SET);
        
        assertNull(filterResource);
    }
    
    @Test
    public void getFilterResourceTestNoFilter() {
        List<String> collection = Arrays.asList("Not", "Annotated", "filter == false");
        
        String filterResource = interceptor.getFilterResource(collection);
        
        assertNull(filterResource);
    }
    
    @Test
    public void getFilterResourceTestIndirectAnnotationFilter() {
        List<IndirectAnnotation> collection = new ArrayList<>(); 
        collection.add(new IndirectAnnotation());
        collection.add(null);
        collection.add(new IndirectAnnotation());
        
        String filterResource = interceptor.getFilterResource(collection);
        
        assertNotNull(filterResource);
        assertEquals("GuardInterfaceImplementation", filterResource);
    }
}
