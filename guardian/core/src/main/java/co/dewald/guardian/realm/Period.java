package co.dewald.guardian.realm;


import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * 
 * @author Dewald Pretorius
 */
@Embeddable
public class Period implements Serializable {
    static final long serialVersionUID = -2139391527524241092L;

    @Column(name = "calendar_field")
    private Integer calendarField;

    @Column(name = "end_value")
    private Integer endValue;

    @Column(name = "start_value")
    private Integer startValue;
    
    public Period() {
        super();
    }
    
    /**
     * @param calendarField
     * @param endValue
     * @param startValue
     */
    public Period(Integer calendarField, Integer startValue, Integer endValue) {
        this();
        
        this.calendarField = calendarField;
        this.endValue = endValue;
        this.startValue = startValue;
    }
    

    /**
     * Answers if the provided timestamp is within this period's boundary.
     * 
     * @param period
     * @param timestamp
     * @return true when the timestamp is within the period boundary
     */
    public boolean in(Date timestamp) {
        if (getCalendarField() == null) return true;
        if (getStartValue() == null && getEndValue() == null) return true;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        
        int value = calendar.get(getCalendarField());
        Integer start = getStartValue();
        Integer end = getEndValue();
        
        if (start != null && start.intValue() > value) return false;
        if (end != null && end.intValue() < value) return false;
        
        return true;
    }

    //@formatter:off
    public Integer getCalendarField() { return calendarField; }
    public void setCalendarField(Integer calendarField) { this.calendarField = calendarField; }

    public Integer getEndValue() { return endValue; }
    public void setEndValue(Integer endValue) { this.endValue = endValue; }

    public Integer getStartValue() { return startValue; }
    public void setStartValue(Integer startValue) { this.startValue = startValue; }
    //@formatter:on
}
