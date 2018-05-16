package org.dulab.adapcompounddb.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FloatMinValidator implements ConstraintValidator<FloatMin, Float> {

    private float min;

    @Override
    public void initialize(FloatMin annotation) {
        min = annotation.value();
    }

    @Override
    public boolean isValid(Float value, ConstraintValidatorContext context) {
        return min <= value;
    }
}
