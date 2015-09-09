package co.dewald.guardian.realm;


import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Dewald Pretorius
 */
public class PeriodTest {

    Period period;
    Calendar calendar;
    
    @Before
    public void setUp() {
        period = new Period();
        calendar = Calendar.getInstance();
    }
    
    @Test
    public void calendarFieldNull() {
        assertNull(period.getCalendarField());
        
        assertTrue(period.in(null));
        assertTrue(period.in(new Date()));
    }
    
    @Test
    public void noStartEndFields() {
        period.setCalendarField(Calendar.YEAR);
        assertNotNull(period.getCalendarField());
        assertNull(period.getStartValue());
        assertNull(period.getEndValue());
        
        assertTrue(period.in(null));
        assertTrue(period.in(new Date()));
    }
    
    @Test(expected = NullPointerException.class)
    public void nullInput() {
        period.setCalendarField(Calendar.YEAR);
        period.setStartValue(2000);
        period.setEndValue(3000);
        assertNotNull(period.getCalendarField());
        assertNotNull(period.getStartValue());
        assertNotNull(period.getEndValue());
        
        period.in(null);
    }
    
    @Test
    public void beforeStart() {
        calendar.add(Calendar.YEAR, 5);
        period.setCalendarField(Calendar.YEAR);
        period.setStartValue(calendar.get(Calendar.YEAR));
        period.setEndValue(null);
        assertNotNull(period.getCalendarField());
        assertNotNull(period.getStartValue());
        assertNull(period.getEndValue());
        
        assertFalse(period.in(new Date()));
    }
    
    @Test
    public void afterStart() {
        calendar.add(Calendar.YEAR, -5);
        period.setCalendarField(Calendar.YEAR);
        period.setStartValue(calendar.get(Calendar.YEAR));
        period.setEndValue(null);
        assertNotNull(period.getCalendarField());
        assertNotNull(period.getStartValue());
        assertNull(period.getEndValue());
        
        assertTrue(period.in(new Date()));
    }
    
    @Test
    public void afterEnd() {
        calendar.add(Calendar.YEAR, -5);
        period.setCalendarField(Calendar.YEAR);
        period.setStartValue(null);
        period.setEndValue(calendar.get(Calendar.YEAR));
        assertNotNull(period.getCalendarField());
        assertNotNull(period.getEndValue());
        assertNull(period.getStartValue());
        
        assertFalse(period.in(new Date()));
    }
    
    @Test
    public void beforeEnd() {
        calendar.add(Calendar.YEAR, 5);
        period.setCalendarField(Calendar.YEAR);
        period.setStartValue(null);
        period.setEndValue(calendar.get(Calendar.YEAR));
        assertNotNull(period.getCalendarField());
        assertNotNull(period.getEndValue());
        assertNull(period.getStartValue());
        
        assertTrue(period.in(new Date()));
    }
    
    @Test
    public void beforePeriod() {
        calendar.add(Calendar.YEAR, 5);
        period.setCalendarField(Calendar.YEAR);
        period.setStartValue(calendar.get(Calendar.YEAR));
        calendar.add(Calendar.YEAR, 5);
        period.setEndValue(calendar.get(Calendar.YEAR));
        assertNotNull(period.getStartValue());
        assertNotNull(period.getCalendarField());
        assertNotNull(period.getEndValue());
        
        assertFalse(period.in(new Date()));
    }
    
    @Test
    public void afterPeriod() {
        calendar.add(Calendar.YEAR, -5);
        period.setCalendarField(Calendar.YEAR);
        period.setEndValue(calendar.get(Calendar.YEAR));
        calendar.add(Calendar.YEAR, -5);
        period.setStartValue(calendar.get(Calendar.YEAR));
        assertNotNull(period.getStartValue());
        assertNotNull(period.getCalendarField());
        assertNotNull(period.getEndValue());
        
        assertFalse(period.in(new Date()));
    }
    
    @Test
    public void inPeriod() {
        calendar.add(Calendar.YEAR, -5);
        period.setCalendarField(Calendar.YEAR);
        period.setStartValue(calendar.get(Calendar.YEAR));
        calendar.add(Calendar.YEAR, 10);
        period.setEndValue(calendar.get(Calendar.YEAR));
        assertNotNull(period.getStartValue());
        assertNotNull(period.getCalendarField());
        assertNotNull(period.getEndValue());
        
        assertTrue(period.in(new Date()));
    }
}
