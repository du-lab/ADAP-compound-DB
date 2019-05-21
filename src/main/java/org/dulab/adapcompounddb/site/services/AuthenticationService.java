package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.validation.Password;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Validated
public interface AuthenticationService {

    UserPrincipal authenticate(
            @NotBlank(message = "The username is required.") String username,
            @NotBlank(message = "The password is required.") String password);

    Optional<UserPrincipal> findUser(long id);

    void saveUser(
            @NotNull(message = "The user principal is required.") @Valid UserPrincipal principal,
            @Password(message = "Please match the requested format.") String password);

    void changePassword(
            String username,
            @Password(message = "Please match the requested format.") String oldpass,
            @Password(message = "Please match the requested format.") String newpass);
}
