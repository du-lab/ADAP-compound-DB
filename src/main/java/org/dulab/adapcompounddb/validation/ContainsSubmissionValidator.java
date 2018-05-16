package org.dulab.adapcompounddb.validation;

import org.dulab.adapcompounddb.models.entities.Submission;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContainsSubmissionValidator implements ConstraintValidator<ContainsSubmission, HttpSession> {

    @Override
    public boolean isValid(HttpSession session, ConstraintValidatorContext context) {
        return Submission.from(session) != null;
    }
}
