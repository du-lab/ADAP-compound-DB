package org.dulab.adapcompounddb.site.controllers;

import javax.servlet.http.HttpSession;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class UserController extends BaseController {

    private final UserPrincipalService userPrincipalService;

    @Autowired
    public UserController(UserPrincipalService userPrincipalService) {
        this.userPrincipalService = userPrincipalService;
    }

    @RequestMapping(value = "/user/{id:\\d+}/delete", method = RequestMethod.GET)
    public String deleteUser(@PathVariable("id") long id, HttpSession session) throws UnsupportedEncodingException {

        UserPrincipal currentUser = getCurrentUserPrincipal();

        if (currentUser != null) {
            if (currentUser.getId() == id) {
                userPrincipalService.delete(id);
                session.setAttribute("currentUser", null);
                return "redirect:/login/";
            } else if (!currentUser.isAdmin())
                return "redirect:/error?errorMsg=" + URLEncoder.encode(
                    "You are not authorized to perform this operation", "UTF-8");
        }
        else{
            return "redirect:/error?errorMsg=" + URLEncoder.encode(
                "You must be logged in to perform this operation", "UTF-8");
        }

        userPrincipalService.delete(id);
        return "redirect:/admin/";
    }
}
