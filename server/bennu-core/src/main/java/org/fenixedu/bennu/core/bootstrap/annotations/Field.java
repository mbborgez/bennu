package org.fenixedu.bennu.core.bootstrap.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Field {
    String name();

    String[] validValues() default {};

    String hint() default "";

    boolean isMandatory() default true;

    FieldType fieldType() default FieldType.TEXT;

    int order();
}
