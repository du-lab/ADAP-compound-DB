package org.dulab.adapcompounddb.validation;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

public class LogicalAOrNotBValidator implements ConstraintValidator<LogicalAOrNotB, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(LogicalAOrNotBValidator.class);

    private String fieldAName;
    private String fieldBName;

    @Override
    public void initialize(LogicalAOrNotB annotation) {
        fieldAName = annotation.fieldA();
        fieldBName = annotation.fieldB();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            boolean isA = Boolean.parseBoolean(BeanUtils.getProperty(value, fieldAName));
            boolean isB = Boolean.parseBoolean(BeanUtils.getProperty(value, fieldBName));
            return isA || !isB;
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.warn(e.getMessage(),e);
            return false;
        }
    }
}
