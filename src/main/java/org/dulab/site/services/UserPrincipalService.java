package org.dulab.site.services;

import org.dulab.models.UserPrincipal;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
public interface UserPrincipalService {

    void saveUserPrincipal(
            @NotNull(message = "User Principal is required.")
            @Valid UserPrincipal userPrincipal);
}
