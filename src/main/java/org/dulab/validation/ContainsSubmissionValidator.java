package org.dulab.validation;

import org.dulab.models.Submission;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContainsSubmissionValidator implements ConstraintValidator<ContainsSubmission, HttpSession> {

    @Override
    public boolean isValid(HttpSession session, ConstraintValidatorContext context) {
        return Submission.from(session) != null;
    }
}
