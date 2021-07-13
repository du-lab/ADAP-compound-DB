package org.dulab.adapcompounddb.validation;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

public class PrivateLibraryValidator implements ConstraintValidator<PrivateLibrary, Object> {

    private static final Logger LOG = LogManager.getLogger();

    private String privateFieldName;
    private String libraryFieldName;

    @Override
    public void initialize(PrivateLibrary annotation) {
        privateFieldName = annotation.privateField();
        libraryFieldName = annotation.libraryField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            boolean isPrivate = Boolean.parseBoolean(BeanUtils.getProperty(value, privateFieldName));
            boolean isLibrary = Boolean.parseBoolean(BeanUtils.getProperty(value, libraryFieldName));
            return isPrivate || !isLibrary;
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.warn(e);
            return false;
        }
    }
}
