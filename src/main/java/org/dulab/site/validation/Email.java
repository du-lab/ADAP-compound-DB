package org.dulab.site.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Pattern(regexp = "^[a-z0-9`!#$%^&*'{}?/+=|_~-]+(\\.[a-z0-9`!#$%^&*'{}?/+=|" +
        "_~-]+)*@([a-z0-9]([a-z0-9-]*[a-z0-9])?)+(\\.[a-z0-9]" +
        "([a-z0-9-]*[a-z0-9])?)*$", flags = {Pattern.Flag.CASE_INSENSITIVE})
@ReportAsSingleViolation
public @interface Email {

    String message() default "Email address is not correct.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
            ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Email[] value();
    }
}
