package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.UserParameter;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.UserParameterType;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
public interface UserPrincipalService {

    void saveUserPrincipal(
            @NotNull(message = "User Principal is required.")
            @Valid UserPrincipal userPrincipal);

    UserParameter findParameter(UserPrincipal userPrincipal, String parameter);

    UserParameter findDefaultParameter(String parameter);

    void saveParameter(UserPrincipal userPrincipal, String parameter, UserParameterType type, Object value);

    void saveDefaultParameter(String parameter, UserParameterType type, Object value);
}
