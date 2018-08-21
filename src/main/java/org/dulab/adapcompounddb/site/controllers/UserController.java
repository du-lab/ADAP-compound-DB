package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
public class UserController extends BaseController {

    private final UserPrincipalService userPrincipalService;

    @Autowired
    public UserController(UserPrincipalService userPrincipalService) {
        this.userPrincipalService = userPrincipalService;
    }

    @RequestMapping(value = "/user/{id:\\d+}/delete", method = RequestMethod.GET)
    public String deleteUser(@PathVariable("id") long id) throws UnsupportedEncodingException {

        UserPrincipal currentUser = getCurrentUserPrincipal();

        if (currentUser == null || !currentUser.isAdmin())
            return "redirect:/error?errorMsg=" + URLEncoder.encode("Only admin can delete users!", "UTF-8");

        userPrincipalService.delete(id);
        return "redirect:/admin/";
    }
}
