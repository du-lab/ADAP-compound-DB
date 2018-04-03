package org.dulab.site.authentication;

import org.dulab.site.models.UserPrincipal;
import org.dulab.site.validation.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
public interface AuthenticationService {

    UserPrincipal authenticate(
            @NotBlank(message = "{validate.authenticate.username}") String username,
            @NotBlank(message = "{validate.authenticate.password}") String password);

    void saveUser(
            @NotNull(message = "{validate.authenticate.saveUser}") @Valid UserPrincipal principal, String password);
}
