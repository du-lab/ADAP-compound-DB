package org.dulab.adapcompounddb.site.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorHandlingController implements ErrorController {

    private static final Logger LOGGER = LogManager.getLogger(ErrorHandlingController.class);


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
