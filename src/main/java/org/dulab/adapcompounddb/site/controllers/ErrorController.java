package org.dulab.adapcompounddb.site.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sun.awt.GlobalCursorManager;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @GetMapping("/errror/notFound")
    @ExceptionHandler(Exception.class)
    public ModelAndView errorPage (HttpServletRequest ht, Exception ex)
    {
        //logger.error("Request: " + ht.getRequestURL() + " raised " + ex);
        ModelAndView err = new ModelAndView("/error/notFound");
        //logger.info("akh:404");
        String errorMsg = "404";
        err.addObject("errorMsg", errorMsg);
        return err;
    }

    @RequestMapping(value = "/error/notFound")
    public String error(Model model) {
        model.addAttribute("errorMsg", "asdfkjhasfkgh");
        return "error/notFound";
    }
}
