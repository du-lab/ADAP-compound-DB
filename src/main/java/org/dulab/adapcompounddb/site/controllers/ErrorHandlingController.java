package org.dulab.adapcompounddb.site.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorHandlingController {

    @RequestMapping(value = "/error")
    public String error(@RequestParam(name = "errorMsg", required = false) String message,
                        Model model) {

        if (message != null)
            model.addAttribute("errorMsg", message);

        return "error";
    }
}
