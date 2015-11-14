package co.dewald.guardian.interceptors;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.interceptor.InvocationContext;
import javax.jws.WebParam;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    
    static final String USER = "subject";
    static final String RESOURCE = "resource";
    static final String ACTION = "action";

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
    
    interface OtherParameterAnnotation {
        void filter(@NotNull String notNull, String nullable, @WebParam(name = "count") Integer soapParam);
    }
    
    interface GrantParameterAnnotation {
        void filter(@Grant(name = "Guardian", filter = true) String notNull, 
                    @Grant(filter = false) Integer soapParam,
                    @Grant(filter = true) String noName);
    }
    
    //@formatter:on

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        interceptor = new GuardInterceptor();
        session = new Session();
        session.setUsername(USER);
        
        interceptor.guardian = guardian;
        interceptor.session = session;
    }
    
    @Test
    public void authoriseTestPass() {
        when(guardian.authorise(USER, RESOURCE, ACTION)).thenReturn(Boolean.TRUE);
        
        interceptor.authorise(USER, RESOURCE, ACTION);
    }
    
    @Test(expected = SecurityException.class)
    public void authoriseTestFalse() throws SecurityException {
        when(guardian.authorise(USER, RESOURCE, ACTION)).thenReturn(Boolean.FALSE);
        
        interceptor.authorise(USER, RESOURCE, ACTION);
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
    
    @Test
    public void grantTestBypass() throws Exception {
        GuardInterceptor interceptorSpy = spy(interceptor);
        InvocationContext ctxMock = mock(InvocationContext.class);
        Method method = DirectAnnotation.class.getDeclaredMethod("action", new Class<?>[0]);   
        
        when(ctxMock.getMethod()).thenReturn(method);
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.FALSE);
        
        Object result = interceptorSpy.grant(ctxMock);
        
        assertNull(result);
        verify(guardian, only()).checkState("DirectAnnotation", "action");
        verify(ctxMock, only()).getMethod();
    }
    
    @Test
    public void grantTestDisabled() throws Exception {
        GuardInterceptor interceptorSpy = spy(interceptor);
        InvocationContext ctxMock = mock(InvocationContext.class);
        Method method = DirectAnnotation.class.getDeclaredMethod("action", new Class<?>[0]);   
        
        when(ctxMock.getMethod()).thenReturn(method);
        when(guardian.checkState(anyString(), anyString())).thenThrow(new SecurityException());
        
        try {
            interceptorSpy.grant(ctxMock);
            fail("The disabled resource should throw a SecurityException");
        } catch (SecurityException e) { 
        }
        
        verify(guardian, only()).checkState("DirectAnnotation", "action");
        verify(ctxMock, only()).getMethod();
    }
    
    @Test
    public void grantTestCheckAuthFalse() throws Exception {
        GuardInterceptor interceptorSpy = spy(interceptor);
        InvocationContext ctxMock = mock(InvocationContext.class);
        Method method = DirectAnnotation.class.getDeclaredMethod("action", new Class<?>[0]);   
        
        when(ctxMock.getMethod()).thenReturn(method);
        when(ctxMock.proceed()).thenReturn("Result");
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.TRUE);
        
        Object result = interceptorSpy.grant(ctxMock);
        
        assertEquals("Result", result);
        verify(ctxMock, times(1)).proceed();
        verify(guardian, only()).checkState("DirectAnnotation", "action");
    }
    
    @Test
    public void grantTestAuthoriseFilterSuccess() throws Exception {
        GuardInterceptor interceptorSpy = spy(interceptor);
        InvocationContext ctxMock = mock(InvocationContext.class);
        Method method = DefaultReflectionFilter.class.getDeclaredMethod("inheritFilter", new Class<?>[0]);
        
        when(ctxMock.getMethod()).thenReturn(method);
        when(ctxMock.proceed()).thenReturn("Result");
        when(guardian.checkState(anyString(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(interceptorSpy).authorise(anyString(), anyString(), anyString());
        doNothing().when(interceptorSpy).filterMethodParameters(any(InvocationContext.class), anyString());
        doReturn("Override").when(interceptorSpy).filter(anyString(), anyString(), anyObject());
        
        Object result = interceptorSpy.grant(ctxMock);
        
        assertEquals("Override", result);
        verify(ctxMock, times(1)).proceed();
        verify(guardian, only()).checkState(DefaultReflectionFilter.class.getName(), "inheritFilter");
        verify(interceptorSpy).authorise(USER, DefaultReflectionFilter.class.getName(), "inheritFilter");
        verify(interceptorSpy).filterMethodParameters(ctxMock, USER);
        verify(interceptorSpy).filter(USER, null, "Result");
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void filterTestMap() {
        GuardInterceptor interceptorSpy = spy(interceptor);
        Map mapMock = mock(Map.class);
        
        when(mapMock.keySet()).thenReturn(Collections.EMPTY_SET);
        when(mapMock.values()).thenReturn(Collections.EMPTY_LIST);
        doNothing().when(interceptorSpy).filterCollection(anyString(), anyString(), anyCollection());
        
        Object result = interceptorSpy.filter(USER, RESOURCE, mapMock);
        
        assertEquals(mapMock, result);
        verify(interceptorSpy, times(1)).filterCollection(USER, RESOURCE, Collections.EMPTY_SET);
        verify(interceptorSpy, times(1)).filterCollection(USER, RESOURCE, Collections.EMPTY_LIST);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void filterTestNonCollection() {
        GuardInterceptor interceptorSpy = spy(interceptor);
        
        doNothing().when(interceptorSpy).filterCollection(anyString(), anyString(), anyCollection());
        
        Object result = interceptorSpy.filter(USER, RESOURCE, null);
        
        assertNull(result);
        verify(interceptorSpy).filterCollection(eq(USER), eq(RESOURCE), anyCollection());
        
        reset(interceptorSpy);
        
        doNothing().when(interceptorSpy).filterCollection(anyString(), anyString(), anyCollection());
        
        result = interceptorSpy.filter(USER, RESOURCE, "Result");
        
        assertEquals("Result", result);
        verify(interceptorSpy).filterCollection(eq(USER), eq(RESOURCE), anyCollection());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void filterTestCollection() {
        GuardInterceptor interceptorSpy = spy(interceptor);
        
        doNothing().when(interceptorSpy).filterCollection(anyString(), anyString(), anyCollection());
        
        Object result = interceptorSpy.filter(USER, RESOURCE, Collections.EMPTY_SET);
        
        assertEquals(Collections.EMPTY_SET, result);
        verify(interceptorSpy).filterCollection(USER, RESOURCE, Collections.EMPTY_SET);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void filterTestImmutableCollections() {
        GuardInterceptor interceptorSpy = spy(interceptor);
        Collection<String> immutableCollection = Collections.EMPTY_LIST;
        
        doThrow(UnsupportedOperationException.class)
            .doNothing()
            .when(interceptorSpy).filterCollection(USER, RESOURCE, Collections.EMPTY_LIST);
        
        Object result = interceptorSpy.filter(USER, RESOURCE, immutableCollection);
        
        assertNotNull(result);
        assertFalse(result == immutableCollection);
        assertTrue(result instanceof List);
        Collection collection = (Collection) result;
        assertTrue(collection.containsAll(immutableCollection));
        
        
        reset(interceptorSpy);
        
        doThrow(UnsupportedOperationException.class)
            .doNothing()
            .when(interceptorSpy).filterCollection(USER, RESOURCE, Collections.EMPTY_SET);
        
        immutableCollection = Collections.EMPTY_SET;
        
        result = interceptorSpy.filter(USER, RESOURCE, immutableCollection);
        
        assertNotNull(result);
        assertFalse(result == immutableCollection);
        assertTrue(result instanceof Set);
        collection = (Collection) result;
        assertTrue(collection.containsAll(immutableCollection));
        
        
        reset(interceptorSpy);
        
        immutableCollection = Collections.unmodifiableCollection(new ArrayDeque());
        
        doThrow(UnsupportedOperationException.class)
            .when(interceptorSpy).filterCollection(USER, RESOURCE, immutableCollection);
        
        try {
            result = interceptorSpy.filter(USER, RESOURCE, immutableCollection);
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void filterMethodParametersTestEmpty() throws Exception {
        InvocationContext ctxMock = mock(InvocationContext.class);
        when(ctxMock.getParameters()).thenReturn(null);

        interceptor.filterMethodParameters(ctxMock, USER);
        verify(ctxMock, only()).getParameters();

        reset(ctxMock);
        when(ctxMock.getParameters()).thenReturn(new Object[0]);

        interceptor.filterMethodParameters(ctxMock, USER);
        verify(ctxMock, only()).getParameters();
    }

    @Test
    public void filterMethodParametersTestFilterParametersDone() throws Exception {
        GuardInterceptor interceptorSpy = spy(interceptor);
        InvocationContext ctxMock = mock(InvocationContext.class);
        
        Object[] parameters = { "method", "parameter", null, Collections.EMPTY_MAP };
        Method method = DirectAnnotation.class.getDeclaredMethod("action", new Class<?>[0]);        

        when(ctxMock.getParameters()).thenReturn(parameters);
        when(ctxMock.getMethod()).thenReturn(method);
        doReturn(true).when(interceptorSpy).filterParameters(USER, method, parameters);
        
        interceptorSpy.filterMethodParameters(ctxMock, USER);
        
        verify(ctxMock, times(1)).getParameters();
        verify(ctxMock, times(1)).getMethod();
        verify(interceptorSpy, times(1)).filterParameters(USER, method, parameters);
    }
    
    @Test
    public void filterMethodParametersTestNoParameterAnnotations() throws Exception {
        GuardInterceptor interceptorSpy = spy(interceptor);
        InvocationContext ctxMock = mock(InvocationContext.class);
        
        Object[] parameters = { "method", "parameter", null, Collections.EMPTY_MAP };
        Method method = DirectAnnotation.class.getDeclaredMethod("action", new Class<?>[0]);   
        
        when(ctxMock.getParameters()).thenReturn(parameters);
        when(ctxMock.getMethod()).thenReturn(method);
        doReturn(false).when(interceptorSpy).filterParameters(USER, method, parameters);
        
        interceptorSpy.filterMethodParameters(ctxMock, USER);
        
        verify(ctxMock, times(1)).getParameters();
        verify(ctxMock, times(1)).getMethod();
        verify(interceptorSpy, times(1)).filterParameters(USER, method, parameters);
    }
    
    @Test
    public void filterMethodParametersTestIndirectMethodParameterAnnotations() throws Exception {
        GuardInterceptor interceptorSpy = spy(interceptor);
        InvocationContext ctxMock = mock(InvocationContext.class);
        
        Object[] parameters = { "method", "parameter", null, Collections.EMPTY_MAP };
        Method method = IndirectAnnotation.class.getDeclaredMethod("polymorphism", new Class<?>[0]);   
        Method icollision = InterfaceCollision.class.getDeclaredMethod("polymorphism", new Class<?>[0]);
        Method imethod = InterfaceAnnotation.class.getDeclaredMethod("polymorphism", new Class<?>[0]);
        
        when(ctxMock.getParameters()).thenReturn(parameters);
        when(ctxMock.getMethod()).thenReturn(method);
        doReturn(false).when(interceptorSpy).filterParameters(USER, method, parameters);
        doReturn(false).when(interceptorSpy).filterParameters(USER, icollision, parameters);
        doReturn(true).when(interceptorSpy).filterParameters(USER, imethod, parameters);
        
        interceptorSpy.filterMethodParameters(ctxMock, USER);
        
        verify(ctxMock, times(1)).getParameters();
        verify(ctxMock, times(1)).getMethod();
        verify(interceptorSpy, times(1)).filterParameters(USER, method, parameters);
        verify(interceptorSpy, times(1)).filterParameters(USER, icollision, parameters);
        verify(interceptorSpy, times(1)).filterParameters(USER, imethod, parameters);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void filterCollectionTestNullEmptyCollection() {
        GuardInterceptor interceptorSpy = spy(interceptor);
        Collection<?> collecionMock = mock(Collection.class);
        
        when(collecionMock.isEmpty()).thenReturn(true);
        
        interceptorSpy.filterCollection(USER, RESOURCE, collecionMock);
        
        verify(collecionMock, only()).isEmpty();
        verify(interceptorSpy, never()).getFilterResource(anyCollection());
        verify(guardian, never()).filter(anyString(), anyString(), anyMap());
        
        
        reset(interceptorSpy, collecionMock, guardian);
        
        interceptorSpy.filterCollection(USER, RESOURCE, null);
        
        verify(interceptorSpy, never()).getFilterResource(anyCollection());
        verify(guardian, never()).filter(anyString(), anyString(), anyMap());
    }
    
    @Test
    public void filterCollectionTestFilterResourceFalse() {
        GuardInterceptor interceptorSpy = spy(interceptor);
        Collection<?> collecionMock = mock(Collection.class);
        
        when(collecionMock.isEmpty()).thenReturn(false);
        doReturn(null).when(interceptorSpy).getFilterResource(collecionMock);
        
        interceptorSpy.filterCollection(USER, null, collecionMock);
        
        verify(collecionMock, only()).isEmpty();
        verify(interceptorSpy, times(1)).getFilterResource(collecionMock);
    }
    
    @SuppressWarnings({"unchecked"})
    @Test
    public void filterCollectionTestMixedCollection() {
        GuardInterceptor interceptorSpy = spy(interceptor);
        List<Object> nonCollection = new ArrayList<>();
        List<Object> mixedCollection = new ArrayList<>();
        mixedCollection.add("Guardian");                
        mixedCollection.add(Collections.EMPTY_MAP);
        mixedCollection.add(new Integer(3));            
        mixedCollection.add(Collections.EMPTY_LIST);
        mixedCollection.add(null);                      
        mixedCollection.add(Collections.EMPTY_SET);
        mixedCollection.add(Boolean.TRUE);             
        nonCollection.add(null); 
        nonCollection.add(Boolean.TRUE);
        
        assertEquals(7, mixedCollection.size());
        doReturn(new HashMap<Long, String>()).when(interceptorSpy).filter(USER, RESOURCE, Collections.EMPTY_MAP);
        doReturn(Arrays.asList("Different", "reference")).when(interceptorSpy)
            .filter(USER, RESOURCE, Collections.EMPTY_LIST); //test substitute immutable collection/map
        doReturn(Collections.EMPTY_SET).when(interceptorSpy).filter(USER, RESOURCE, Collections.EMPTY_SET);
        when(guardian.filter(eq(USER), eq(RESOURCE), any(Map.class))).thenReturn(nonCollection);
        
        interceptorSpy.filterCollection(USER, RESOURCE, mixedCollection);
        
        assertEquals(5, mixedCollection.size());
        assertFalse(mixedCollection.contains("Guardian"));
        assertFalse(mixedCollection.contains(3));
        assertTrue(mixedCollection.containsAll(nonCollection));
        assertTrue(mixedCollection.contains(Collections.EMPTY_MAP));
        assertFalse(mixedCollection.contains(Collections.EMPTY_LIST)); //test substitution
        assertTrue(mixedCollection.contains(Collections.EMPTY_SET));
        verify(guardian, only()).filter(anyString(), anyString(), anyMap());
        verify(interceptorSpy, times(3)).filter(anyString(), anyString(), anyObject());
    }
    
    @Test
    public void filterParametersTestNoParameters() throws Exception {
        GuardInterceptor interceptorSpy = spy(interceptor);
        Class<?> classMock = IndirectAnnotation.class;
        Method method = classMock.getDeclaredMethod("polymorphism", new Class<?>[0]);
        
        boolean filtered = interceptorSpy.filterParameters(USER, method, new Class<?>[0]);
        
        assertFalse(filtered);
        verify(interceptorSpy, only()).filterParameters(eq(USER), eq(method), eq(new Class<?>[0]));
    }
    
    @Test
    public void filterParametersTestNullGrant() throws Exception {
        GuardInterceptor interceptorSpy = spy(interceptor);
        Class<?>[] parameters = {String.class, String.class, Integer.class};
        Object[] data = new Object[] {"Not Null", null, new Integer(3)};
        Method method = OtherParameterAnnotation.class.getDeclaredMethod("filter", parameters);
        
        boolean filtered = interceptorSpy.filterParameters(USER, method, data);
        
        assertFalse(filtered);
        verify(interceptorSpy, times(1)).filterParameters(eq(USER), eq(method), eq(data));
    }
    
    @Test
    public void filterParametersTestGrant() throws Exception {
        GuardInterceptor interceptorSpy = spy(interceptor);
        Class<?>[] parameters = {String.class, Integer.class, String.class};
        Object[] data = new Object[] {"Filtered", new Integer(-1), "No Name"};
        Method method = GrantParameterAnnotation.class.getDeclaredMethod("filter", parameters);
        
        doReturn("first").doReturn("last").when(interceptorSpy).filter(anyString(), anyString(), any());
        
        boolean filtered = interceptorSpy.filterParameters(USER, method, data);
        
        assertTrue(filtered);
        verify(interceptorSpy, times(1)).filter(eq(USER), eq((String) null), eq("No Name"));
        verify(interceptorSpy, times(1)).filter(eq(USER), eq("Guardian"), eq("Filtered"));
    }
}
