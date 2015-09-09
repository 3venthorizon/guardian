package co.dewald.guardian;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import co.dewald.guardian.gate.Grant;
import co.dewald.guardian.gate.Guardian;


/**
 * 
 * @author Dewald Pretorius
 */
@RunWith(MockitoJUnitRunner.class)
public class GIFilterTest {
    
    static final String USER = "username";
    static final String RESOURCE = "resource";
    
    @Grant(name = RESOURCE)
    class NoFilter {}
    
    @Grant(name = RESOURCE, filter = true)
    class Data {}
    
    @Grant(filter = true)
    class NoName {}
    
    
    GuardInterceptor interceptor;
    @Mock Guardian guardian;
    
    List<String> list;
    Collection<String> collection;

    @Before
    public void setUp() throws Exception {
        interceptor = new GuardInterceptor();
        interceptor.guardian = guardian;
        list = Arrays.asList("Null", "Map", "Key", "Elements", null, "Allowed");
        collection = new ArrayList<String>(list);
    }

    @Test
    public void extractResourceNullGrant() {
        String resource = interceptor.extractFilteredResource(String.class);
        
        assertNull(resource);
    }
    
    @Test 
    public void extractFalseFilterResource() {
        String resource = interceptor.extractFilteredResource(NoFilter.class);
        
        assertNull(resource);
    }
    
    @Test
    public void extractFilteredData() {
        String resource = interceptor.extractFilteredResource(Data.class);
        
        assertEquals(RESOURCE, resource);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void extractCollectionNullCollection() {
        String resource = interceptor.extractFilteredResource((Collection) null);
        
        assertNull(resource);
    }
    
    @Test
    public void extractCollectionEmptyCollection() {
        String resource = interceptor.extractFilteredResource(Collections.emptyList());
        
        assertNull(resource);
    }
    
    @Test
    public void extractCollectionNullGrant() {
        Collection<String> collection = Arrays.asList("Null", "Map", "Key", "Elements", null, "Allowed");
        
        String resource = interceptor.extractFilteredResource(collection);
        
        assertNull(resource);
    }
    
    @Test
    public void extractFalseFilterCollection() {
        Collection<NoFilter> collection = Arrays.asList(null, null, new NoFilter());
        
        String resource = interceptor.extractFilteredResource(collection);
        
        assertNull(resource);
    }
    
    @Test
    public void extractFilteredCollection() {
        Collection<Data> collection = Arrays.asList(null, null, new Data());
        
        String resource = interceptor.extractFilteredResource(collection);
        
        assertEquals(RESOURCE, resource);
    }
    
    @Test
    public void extractNoNameResource() {
        Collection<NoName> collection = Arrays.asList(null, null, new NoName());
        
        String resource = interceptor.extractFilteredResource(collection);
        
        assertEquals(null, resource);
    }
    
    @Test
    public void filterNullCollection() {
        interceptor.filterCollection(USER, RESOURCE, null);
        
        verifyZeroInteractions(guardian);
    }
    
    @Test
    public void filterEmptyCollections() {
        interceptor.filterCollection(USER, RESOURCE, Collections.emptyList());
        
        verifyZeroInteractions(guardian);
    }

    @Test
    public void filterEmptyResource() {
        Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
        int count = collection.size();
        
        interceptor.filterCollection(USER, "", collection);
        
        verifyZeroInteractions(guardian);
        assertEquals(count, collection.size());
        
        int match = 1;
        
        for (Integer element : collection) {
            assertEquals(match++, element.intValue());
        }
    }
    
    @Test
    public void filterNullResourceNullGrant() {
        Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
        int count = collection.size();
        
        interceptor.filterCollection(USER, null, collection);
        
        verifyZeroInteractions(guardian);
        assertEquals(count, collection.size());
        
        int match = 1;
        
        for (Integer element : collection) {
            assertEquals(match++, element.intValue());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test(expected = UnsupportedOperationException.class)
    public void filterNullUnsupportedRemovals() {
        when(guardian.filter(eq(USER), eq(RESOURCE), anyMap())).thenReturn(null);

        try {
            interceptor.filterCollection(USER, RESOURCE, list);
        } catch (UnsupportedOperationException uoe) {
            verify(guardian).filter(eq(USER), eq(RESOURCE), anyMap());
            verify(guardian, times(1)).filter(eq(USER), eq(RESOURCE), anyMap());
            throw uoe;
        }

        fail("This collection does not support remove operations");
    }
    
    @SuppressWarnings("unchecked")
    @Test(expected = UnsupportedOperationException.class)
    public void filterEmptyUnsupportedRemovals() {
        when(guardian.filter(eq(USER), eq(RESOURCE), anyMap())).thenReturn(new ArrayList<String>());

        try {
            interceptor.filterCollection(USER, RESOURCE, list);
        } catch (UnsupportedOperationException uoe) {
            verify(guardian).filter(eq(USER), eq(RESOURCE), anyMap());
            verify(guardian, times(1)).filter(eq(USER), eq(RESOURCE), anyMap());
            throw uoe;
        }

        fail("This collection does not support remove operations");
    }
    
    @SuppressWarnings({"unchecked"})
    @Test(expected = UnsupportedOperationException.class)
    public void filterSubSetUnsupportedRemovals() {
        collection.remove("Map");
        when(guardian.filter(eq(USER), eq(RESOURCE), anyMap())).thenReturn((List<String>) collection);

        try {
            interceptor.filterCollection(USER, RESOURCE, list);
        } catch (UnsupportedOperationException uoe) {
            verify(guardian).filter(eq(USER), eq(RESOURCE), anyMap());
            verify(guardian, times(1)).filter(eq(USER), eq(RESOURCE), anyMap());
            throw uoe;
        }

        fail("This collection does not support remove operations");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void fiterNullElements() {
        when(guardian.filter(eq(USER), eq(RESOURCE), anyMap())).thenReturn(null);
        
        interceptor.filterCollection(USER, RESOURCE, collection);
        
        assertEquals(0, collection.size());
        verify(guardian).filter(eq(USER), eq(RESOURCE), anyMap());
        verify(guardian, times(1)).filter(eq(USER), eq(RESOURCE), anyMap());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void filterGuardianReturnEmpty() {
        when(guardian.filter(eq(USER), eq(RESOURCE), anyMap())).thenReturn(Collections.emptyList());
        
        interceptor.filterCollection(USER, RESOURCE, collection);
        
        assertEquals(0, collection.size());
        verify(guardian).filter(eq(USER), eq(RESOURCE), anyMap());
        verify(guardian, times(1)).filter(eq(USER), eq(RESOURCE), anyMap());
    }
    
    @SuppressWarnings("unchecked")
    @Test 
    public void filterGuardianReturnAll() {
        when(guardian.filter(eq(USER), eq(RESOURCE), anyMap())).thenReturn(list);
        
        interceptor.filterCollection(USER, RESOURCE, collection);
        
        assertEquals(list.size(), collection.size());
        verify(guardian).filter(eq(USER), eq(RESOURCE), anyMap());
        verify(guardian, times(1)).filter(eq(USER), eq(RESOURCE), anyMap());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void filterGuardianReturnSubset() {
        List<String> subset = Arrays.asList(null, "Allowed");
        when(guardian.filter(eq(USER), eq(RESOURCE), anyMap())).thenReturn(subset);
        
        interceptor.filterCollection(USER, RESOURCE, collection);
        
        assertEquals(2, collection.size());
        Iterator<String> iterator = collection.iterator();
        assertNull(iterator.next());
        assertEquals("Allowed", iterator.next());
        verify(guardian).filter(eq(USER), eq(RESOURCE), anyMap());
        verify(guardian, times(1)).filter(eq(USER), eq(RESOURCE), anyMap());
    }
 
    @SuppressWarnings("unchecked")
    @Test
    public void filterGuardianReturnMisMatch() {
        List<String> mismatch = Arrays.asList("Not", "in", "the", "list?");
        when(guardian.filter(eq(USER), eq(RESOURCE), anyMap())).thenReturn(mismatch);
        
        interceptor.filterCollection(USER, RESOURCE, collection);
        
        assertEquals(0, collection.size());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void nestedCollections() {
        List<String> retain = Arrays.asList("Key", "Elements", null);
        when(guardian.filter(eq(USER), eq(RESOURCE), anyMap())).thenReturn(retain);
        List<List<String>> squared = new ArrayList<>();
        List<String> left = new ArrayList<>();
        List<String> right = new ArrayList<>();
        left.addAll(Arrays.asList("Null", "Map", "Key"));
        right.addAll(Arrays.asList("Elements", null, "Allowed"));
        squared.add(left); 
        squared.add(right);
        
        interceptor.filterCollection(USER, RESOURCE, squared);
        
        verify(guardian, times(2)).filter(eq(USER), eq(RESOURCE), anyMap());
        assertEquals(2, squared.size());
        assertEquals(1, left.size());
        assertEquals(2, right.size());
        assertEquals("Key", left.get(0));
        assertEquals("Elements", right.get(0));
        assertNull(right.get(1));
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void nestedMapCollections() {
        List<String> retain = Arrays.asList("Key", "Elements");
        when(guardian.filter(eq(USER), eq(RESOURCE), anyMap())).thenReturn(retain);
        Collection<Map> collection = new ArrayList<>();
        Map<String, List<Map<String, String>>> superMap = new LinkedHashMap<>();
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> keyKey = new LinkedHashMap<>();
        Map<String, String> valVal = new LinkedHashMap<>();
        Map<String, String> mapMap = new LinkedHashMap<>();
        collection.add(superMap);
        collection.add(mapMap);
        superMap.put("Elements", mapList);
        mapList.add(keyKey);
        mapList.add(valVal);
        
        for (String element : list) {
            if (element == null) continue;
            
            keyKey.put(element, element);
            valVal.put(element, element);
            mapMap.put(element, element);
        }
        
        interceptor.filterCollection(USER, RESOURCE, collection);
        
        assertEquals(2, keyKey.keySet().size());
        assertEquals(2, valVal.keySet().size());
        assertEquals(2, mapMap.keySet().size());
        assertTrue(keyKey.keySet().containsAll(retain));
        assertTrue(keyKey.values().containsAll(retain));
        assertTrue(valVal.keySet().containsAll(retain));
        assertTrue(valVal.values().containsAll(retain));
        assertTrue(mapMap.keySet().containsAll(retain));
        assertTrue(mapMap.values().containsAll(retain));
    }
}
