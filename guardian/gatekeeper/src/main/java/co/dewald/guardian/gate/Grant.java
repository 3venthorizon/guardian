package co.dewald.guardian.gate;


import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;


/**
 * Mark resource-actions and grant access from granular to fine:
 * <ul>
 *     <li>Resource: Class</li>
 *     <li>Action: Method</li>
 *     <li>Filter: Parameter</li>
 * </ul>
 * @author Dewald Pretorius
 */
@Inherited
@Target({TYPE, METHOD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Grant {
    
    /**
     * Designates a resource or action when annotated on a class or method respectively. 
     */
    String name() default "";
    
    /**
     * Action authorisation check is by default true.
     */
    boolean check() default true;
    
    /**
     * Specifies whether a parameter/result filter should be applied where the default is false or no filtering. 
     * The {@link Collection} or return object will be culled. 
     */
    boolean filter() default false;
}
