package co.dewald.guardian;


import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.dewald.guardian.gate.Grant;


/**
 * @see GuardInterceptor#filter(String, String, Object)
 * 
 * @author Dewald Pretorius
 */
public class GIFilterObjectTest {
    private static final String USER = "username";
    private static final String RESOURCE = "resource";
    
    @Grant(name = RESOURCE, filter = true)
    class Data { }
    
    class PartialInterceptor extends GuardInterceptor {
        int callCount;
        String username;
        String resource;
        Collection<?> collection;
        Collection<?> removals;
        boolean immutable = true;
        boolean clear = false;
        
        /**
         * Functionality not inherited.
         * 
         * @param username
         * @param resource
         * @param collection
         */
        @Override
        <T> void filterCollection(String username, String resource, Collection<T> collection) {
            callCount++;
            this.username = username;
            this.resource = resource;
            this.collection = collection;
            
            if (collection.isEmpty()) return;
            T element = collection.iterator().next();
            
            if (immutable) {
                collection.remove(element); //must not throw UnsupportedOperationException
                collection.add(element); //must not throw UnsupportedOperationException
            }
            
            if (removals != null) collection.removeAll(removals);
            if (clear) collection.clear();
        }
    }
    
    PartialInterceptor interceptor;
    
    @Before
    public void prepare() {
        interceptor = new PartialInterceptor();
    }
    
    @Test 
    public void unfiltered() {
        try {
            Object filtered = interceptor.filter(USER, RESOURCE, "String Result");
            
            assertEquals("String Result", filtered);
            assertEquals(1, interceptor.callCount);
            assertFalse(interceptor.collection.isEmpty());
        } catch(UnsupportedOperationException uoe) {
            fail("Immutable collection passed to the " + 
                 "GuardInterceptor#filterCollection(String, String, java.util.Collection)");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void filtered() {
        try {
            interceptor.clear = true;
            
            Object filtered = interceptor.filter(USER, RESOURCE, new Data());
            
            assertNull(filtered);
            assertEquals(1, interceptor.callCount);
        } catch(UnsupportedOperationException uoe) {
            fail("Immutable collection passed to the " + 
                 "GuardInterceptor#filterCollection(String, String, java.util.Collection)");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @SuppressWarnings({ "unchecked" })
    @Test
    public void immutableList() {
        try {
            List<String> object = Arrays.asList("Null", "Map", "Key", "Elements", null, "Allowed");
            
            Object filtered = interceptor.filter(USER, RESOURCE, object);
            
            assertEquals(2, interceptor.callCount);
            assertTrue(filtered instanceof List);
            List<String> resultList = (List<String>) filtered;
            assertTrue(object.containsAll(resultList));
            assertTrue(resultList.containsAll(object));
        } catch(UnsupportedOperationException uoe) {
            fail("Immutable collection passed to the " + 
                 "GuardInterceptor#filterCollection(String, String, java.util.Collection)");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @SuppressWarnings({ "unchecked" })
    @Test
    public void immutableSet() {
        try {
            List<String> object = Arrays.asList("Null", "Map", "Key", "Elements", null, "Allowed");
            Set<String> immutable = Collections.unmodifiableSet(new HashSet<>(object));
            
            Object filtered = interceptor.filter(USER, RESOURCE, immutable);
            
            assertEquals(2, interceptor.callCount);
            assertTrue(filtered instanceof Set);
            Set<String> resultList = (Set<String>) filtered;
            assertTrue(object.containsAll(resultList));
            assertTrue(resultList.containsAll(object));
        } catch(UnsupportedOperationException uoe) {
            fail("Immutable collection passed to the " + 
                 "GuardInterceptor#filterCollection(String, String, java.util.Collection)");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void immutableCollection() throws Exception {
        try {
            List<String> object = Arrays.asList("Null", "Map", "Key", "Elements", "Allowed");
            PriorityQueue<String> queue = new PriorityQueue<>(object);
            Collection<String> immutable = Collections.unmodifiableCollection(queue);
            
            interceptor.filter(USER, RESOURCE, immutable);
            fail();
        } catch(UnsupportedOperationException uoe) {
            assertEquals(1, interceptor.callCount);
            throw uoe;
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void collectionFiltered() {
        try {
            interceptor.clear = true;
            List<String> object = Arrays.asList("Null", "Map", "Key", "Elements", null, "Allowed");
            
            Object filtered = interceptor.filter(USER, RESOURCE, object);
            
            assertEquals(2, interceptor.callCount);
            assertTrue(filtered instanceof List);
            List<String> resultList = (List<String>) filtered;
            assertTrue(resultList.isEmpty());
        } catch(UnsupportedOperationException uoe) {
            fail("Immutable collection passed to the " + 
                 "GuardInterceptor#filterCollection(String, String, java.util.Collection)");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void mapFiltered() {
        interceptor.immutable = false;
        interceptor.removals = Arrays.asList("2", "four");
        
        Map<String, String> object = new LinkedHashMap<>();
        object.put("1", "one");
        object.put("2", "two");
        object.put("3", "three");
        object.put("4", "four");
        object.put("5", "five");
        
        Object filtered = interceptor.filter(USER, RESOURCE, object);
        
        assertEquals(2, interceptor.callCount);
        assertTrue(filtered instanceof Map);
        Map<String, String> resultMap = (Map<String, String>) filtered;
        assertEquals(3, resultMap.size());
    }
    
    @After
    public void evaluate() {
        assertEquals(USER, interceptor.username);
        assertEquals(RESOURCE, interceptor.resource);
    }
}
