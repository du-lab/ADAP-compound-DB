package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.controllers.forms.ResetPasswordForm;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class OrganizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordController.class);
    @Autowired
    UserPrincipalService userPrincipalService;

    @GetMapping("/organization/addUser")
    public String addToOrganization(Model model, @RequestParam("token") String token,
                                    @RequestParam("orgEmail") String orgEmail) throws Exception {
        UserPrincipal orgUser = userPrincipalService.findByUserEmail(orgEmail);
        UserPrincipal user = userPrincipalService.findByOrganizationToken(token);
        if(user == null || orgUser == null)
            throw new Exception("Invalid token");
        if(user.getOrganizationRequestExpirationDate().before(new Date()))
            throw new Exception("Reset link has expired");
        user.setOrganizationId(orgUser.getId());
        user.setOrganizationRequestToken(null);
        user.setOrganizationRequestExpirationDate(null);
        userPrincipalService.saveUserPrincipal(user);
        return "/account/organization_add_success";
    }
}
