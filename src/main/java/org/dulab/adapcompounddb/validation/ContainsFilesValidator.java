package org.dulab.adapcompounddb.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;

public class ContainsFilesValidator implements ConstraintValidator<ContainsFiles, List<MultipartFile>> {

    @Override
    public boolean isValid(List<MultipartFile> multipartFiles, ConstraintValidatorContext context) {
        return multipartFiles.size() == multipartFiles.stream()
                .filter(Objects::nonNull)
                .filter(f -> f.getSize() > 0)
                .count();
    }
}
