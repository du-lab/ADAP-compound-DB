package org.dulab.controllers;

import org.dulab.models.Spectrum;
import org.dulab.services.FileReaderService;
import org.dulab.services.MspFileReaderService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
public class FileController {

    private FileReaderService fileReaderService;

    public FileController() {
        fileReaderService = new MspFileReaderService();
    }

    @RequestMapping(value="/file/upload", method=RequestMethod.POST, consumes={"multipart/form-data"})
    public View fileUpload(Model model,
                           HttpSession session,
                           @RequestParam("file") MultipartFile file)
            throws IOException {

        if (file.getSize() == 0) {
            model.addAttribute("message", "Uploaded file is empty");
            return new RedirectView("/file/upload");
        }

        List<Spectrum> spectra = fileReaderService.read(file.getInputStream());
        if (spectra == null || spectra.isEmpty()) {
            model.addAttribute("message", "Cannot read this file");
            return new RedirectView("/file/upload");
        }

        session.setAttribute("spectrumList", spectra);
        session.setAttribute("fileName", file.getOriginalFilename());
        return new RedirectView("/file");
    }

    @RequestMapping(value={"/file", "/file/upload"}, method=RequestMethod.GET)
    public String file(HttpSession session) {

        if (session.getAttribute("spectrumList") != null)
            return "file";

        return "upload";
    }

    @RequestMapping(value="/file/{spectrumId:\\d+}", method=RequestMethod.GET)
    public String spectrum(@PathVariable("spectrumId") int spectrumId,
                           Model model,
                           HttpSession session) {

        @SuppressWarnings("unchecked")
        List<Spectrum> spectrumList = (List<Spectrum>) session.getAttribute("spectrumList");
        Spectrum spectrum = spectrumList.get(spectrumId);

        model.addAttribute("name", spectrum.toString());
        model.addAttribute("properties", spectrum.getProperties());

        // Generate JSON string with mz-values and intensities
        double[] mzValues = spectrum.getMzValues();
        double[] intensities = spectrum.getIntensities();
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < mzValues.length; ++i) {
            if (i != 0)
                stringBuilder.append(',');
            stringBuilder.append('[').append(mzValues[i]).append(',').append(intensities[i]).append(']');
        }
        stringBuilder.append(']');

        model.addAttribute("jsonPeaks", stringBuilder.toString());

        return "spectrum";
    }
}
