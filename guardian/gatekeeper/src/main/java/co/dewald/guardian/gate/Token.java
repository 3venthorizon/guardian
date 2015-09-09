package co.dewald.guardian.gate;


import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Dewald Pretorius
 */
@Inherited
@Target({PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {
}
