package org.dulab.site.submission;

import org.dulab.site.models.Peak;
import org.dulab.site.models.Spectrum;
import org.dulab.site.models.Submission;
import org.dulab.site.models.UserPrincipal;
import org.dulab.site.validation.ContainsSubmission;
import org.dulab.site.validation.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class FileViewController {

    private SubmissionService submissionService;

    public FileViewController() {
        submissionService = new DefaultSubmissionService();
    }

    @RequestMapping(value = "file/view", method = RequestMethod.GET)
    public String view(@ContainsSubmission HttpSession session, Model model) {
        model.addAttribute("form", new Form());
        return "file/view";
    }

    @RequestMapping(value = "file/view/{submissionId:\\d+}", method = RequestMethod.GET)
    public String viewSubmission(Model model, @PathVariable("submissionId") long submissionId) {
        Submission submission = submissionService.findSubmission(submissionId);
        if (submission == null)
            return "file/submissionnotfound";
        model.addAttribute("submission", submission);
        return "file/view";
    }

    @RequestMapping(value = "file/view", method = RequestMethod.POST)
    public String submit(@ContainsSubmission HttpSession session, Model model, @Valid Form form, Errors errors) {
        if (errors.hasErrors())
            return "file/view";

        Submission submission = Submission.from(session);
        submission.setName(form.getName());
        submission.setDescription(form.getDescription());
        submission.setDateTime(new Date());
        submission.setUser(UserPrincipal.from(session));

        try {
            submissionService.saveSubmission(submission);
        }
        catch (ConstraintViolationException e) {
            model.addAttribute("validationErrors", e.getConstraintViolations());
            return "file/view";
        }

        model.addAttribute("message", "Mass spectra are submitted successfully.");
        return "file/view";
    }

    @RequestMapping(value = "file/raw/view", method = RequestMethod.GET)
    public void rawView(@ContainsSubmission HttpSession session, HttpServletResponse response) throws IOException {

        Submission submission = Submission.from(session);
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "inline; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }

    @RequestMapping(value = "file/raw/download", method = RequestMethod.GET)
    public void rawDownload(@ContainsSubmission HttpSession session, HttpServletResponse response) throws IOException {

        Submission submission = Submission.from(session);
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }

    @RequestMapping(value = "file/{spectrumId:\\d+}", method = RequestMethod.GET)
    public String spectrum(@PathVariable("spectrumId") int spectrumId,
                           Model model,
                           @ContainsSubmission HttpSession session) {

        Submission submission = Submission.from(session);
        Spectrum spectrum = submission.getSpectra().get(spectrumId);

        model.addAttribute("name", spectrum.toString());
        model.addAttribute("properties", spectrum.getProperties());

        // Generate JSON string with mz-values and intensities
        List<Peak> peaks = spectrum.getPeaks();

        double maxIntensity = peaks.stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElseThrow(() -> new IllegalStateException("Cannot determine maximum intensity of the peak list"));

        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < peaks.size(); ++i) {
            if (i != 0)
                stringBuilder.append(',');
            stringBuilder.append('[')
                    .append(peaks.get(i).getMz())
                    .append(',')
                    .append(100 * peaks.get(i).getIntensity() / maxIntensity)
                    .append(']');
        }
        stringBuilder.append(']');

        model.addAttribute("jsonPeaks", stringBuilder.toString());

        return "file/spectrum";
    }




    public static class Form {

        @NotBlank(message = "The field Name is required.")
        private String name;

        @NotBlank(message = "The field Description is required.")
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
