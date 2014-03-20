package org.fenixedu.bennu.core.bootstrap.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Bootstrapper {
    Class<?>[] sections();

    String name();

    String description() default "";

    Class<?>[] after() default {};

    String bundle();

}
