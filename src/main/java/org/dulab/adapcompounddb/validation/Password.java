package org.dulab.adapcompounddb.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;

/************************************************
***** RegExp pattern for password validation *****
*
 * ^                start of string
 * (?=.*[0-9])      contains at least one digit
 * (?=.*[a-z])      contains at least one lower case letter
 * (?=.*[A-Z])      contains at least one upper case letter
 * (?=.*[@#$%^&+=]) contains at least one special character
 * (?=\S+$)         no whitespace allowed
 * .{8,}            at least eight characters long
 * $                end of string
 *
***********************************************/

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
        flags = {Pattern.Flag.CASE_INSENSITIVE})
@ReportAsSingleViolation
public @interface Password {

    String message() default "Password is not correct.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
            ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Password[] value();
    }
}
