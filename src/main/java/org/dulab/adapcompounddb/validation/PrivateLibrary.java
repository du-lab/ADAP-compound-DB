package org.dulab.adapcompounddb.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PrivateLibraryValidator.class)
@Documented
public @interface PrivateLibrary {

    String message() default "Creating a library is allowed only for private submissions";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String privateField();

    String libraryField();

    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        PrivateLibrary[] value();
    }
}
