package org.dulab.adapcompounddb.site.controllers;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@Controller
public class ErrorHandlingController implements ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlingController.class);

    private static String pageNotFoundMsg = "The page you requested was not found.";
    @RequestMapping(value = "/error")
    public String error(@RequestParam(name = "errorMsg", required = false) String message,
                        Model model, HttpServletRequest request, HttpServletResponse response) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value() ) {
                response.setStatus(HttpStatus.OK.value());
                model.addAttribute("errorMsg", pageNotFoundMsg );
                return "redirect:404";
            }
        }
        if (message != null) {
            model.addAttribute("errorMsg", message);
        }
        return "error";
    }
    @RequestMapping(value="/404")
    public String pageNotFoundHandler(){
        return "404";
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
