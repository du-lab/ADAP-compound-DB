package org.dulab.site.services;

import org.dulab.models.UserPrincipal;
import org.dulab.validation.NotBlank;
import org.dulab.validation.Password;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
public interface AuthenticationService {

    UserPrincipal authenticate(
            @NotBlank(message = "The username is required.") String username,
            @NotBlank(message = "The password is required.") String password);

    UserPrincipal findUser(long id);

    void saveUser(
            @NotNull(message = "The user principal is required.") @Valid UserPrincipal principal,
            @Password(message = "Please match the requested format.") String password);
}
