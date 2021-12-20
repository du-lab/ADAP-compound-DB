package org.dulab.adapcompounddb.site.controllers.ajax;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class SpectrumAjaxController {

    private final SpectrumService spectrumService;

    @Autowired
    public SpectrumAjaxController(SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }

    @RequestMapping(value = "/ajax/spectrum/info", method = RequestMethod.GET)
    public String spectrumInfo(@RequestParam Long spectrumId,
                               @RequestParam(required = false) Integer fileIndex,
                               @RequestParam(required = false) Integer spectrumIndex,
                               HttpSession session, Model model) {

        Spectrum spectrum = null;
        if (spectrumId != null && spectrumId > 0)
            spectrum = spectrumService.find(spectrumId);

        else if (spectrumIndex != null && fileIndex != null) {
            Submission submission = Submission.from(session);
            if (submission != null)
                spectrum = submission
                        .getFiles().get(fileIndex)
                        .getSpectra().get(spectrumIndex);
        }

        if (spectrum == null)
            return null;

        model.addAttribute("spectrum", spectrum);

        return "ajax/spectrumInfo";
    }
}
