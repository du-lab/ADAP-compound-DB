package org.dulab.adapcompounddb.site.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorHandlingController implements ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlingController.class);


    @RequestMapping(value = "/error")
    public String error(@RequestParam(name = "errorMsg", required = false) String message,
                        Model model) {

        if (message != null) {
            model.addAttribute("errorMsg", message);
        }

        return "error";
    }

    @RequestMapping(value = "/js-log", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logJavaScriptError(HttpServletRequest request, @RequestBody String message) {
        String ipAddress = request.getRemoteAddr();
        String hostname = request.getRemoteHost();
        LOGGER.warn(String.format("Received client-side log message (%s/%s): %s",
                ipAddress, hostname, message));
    }

}
