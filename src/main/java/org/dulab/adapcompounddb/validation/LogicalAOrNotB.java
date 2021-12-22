package org.dulab.adapcompounddb.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LogicalAOrNotBValidator.class)
@Documented
public @interface LogicalAOrNotB {

    String message() default "Invalid field values";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String fieldA();

    String fieldB();

    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        LogicalAOrNotB[] value();
    }
}
