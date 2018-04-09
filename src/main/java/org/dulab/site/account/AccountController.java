package org.dulab.site.account;

import org.dulab.site.models.UserPrincipal;
import org.dulab.site.validation.ContainsUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

@Controller
public class AccountController {

    @RequestMapping(value = "account", method = RequestMethod.GET)
    public View account() {
        return new RedirectView("account/view");
    }

    @RequestMapping(value = "account/view", method = RequestMethod.GET)
    public String view(@ContainsUser HttpSession session, Model model) {
        model.addAttribute("user", UserPrincipal.from(session));
        return "account/view";
    }
}
