package org.dulab.adapcompounddb.site.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DownloadController {

    @RequestMapping(value = "downloads/", method = RequestMethod.GET)
    public String download(Model model) {
        return "downloads";
    }
}
