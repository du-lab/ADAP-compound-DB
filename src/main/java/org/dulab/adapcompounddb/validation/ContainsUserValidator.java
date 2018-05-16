package org.dulab.adapcompounddb.validation;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContainsUserValidator implements ConstraintValidator<ContainsSubmission, HttpSession> {

    @Override
    public boolean isValid(HttpSession session, ConstraintValidatorContext context) {
        return UserPrincipal.from(session) != null;
    }
}
