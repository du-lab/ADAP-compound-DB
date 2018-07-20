package org.dulab.adapcompounddb.validation;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private static final Logger LOG = LogManager.getLogger();

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch annotation) {
        firstFieldName = annotation.first();
        secondFieldName = annotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Object first = BeanUtils.getProperty(value, firstFieldName);
            Object second = BeanUtils.getProperty(value, secondFieldName);

            return first == null && second == null
                    || first != null && first.equals(second);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.warn(e);
        	e.printStackTrace();
            return false;
        }
    }
}
