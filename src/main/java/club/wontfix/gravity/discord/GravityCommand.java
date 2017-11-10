package club.wontfix.gravity.discord;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GravityCommand {

    String[] value();

    boolean allowPublic() default true;

    String description() default "";

}
