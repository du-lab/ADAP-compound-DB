package org.dulab.controllers;

import org.dulab.models.Spectrum;
import org.dulab.models.readers.MspReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

//@WebServlet(
//        name = "fileController",
//        urlPatterns = {"/newfile"}
//)
//@MultipartConfig(
//        fileSizeThreshold = 5242880,  // 5MB
//        maxFileSize = 20971520L,  // 20MB
//        maxRequestSize = 41943040L  // 40MB
//)

@Controller
public class FileController {

    @RequestMapping(value="/file/upload", method=RequestMethod.POST, consumes={"multipart/form-data"})
    public String fileUpload(Model model,
                             HttpSession session,
                             @RequestPart("file1") Part filePart)
            throws IOException {

        if (filePart.getSize() == 0) {
            model.addAttribute("emptyFile", false);
            return "upload";
        }

        List<Spectrum> spectra = MspReader.read(filePart.getInputStream());
        if (spectra == null || spectra.isEmpty()) {
            model.addAttribute("emptyFile", false);
            return "upload";
        }


        session.setAttribute("spectrumList", spectra);
        session.setAttribute("fileName", filePart.getSubmittedFileName());
        return "submission";
    }

    @RequestMapping(value="/file/upload", method=RequestMethod.GET)
    public String fileUpload(HttpSession session) {

        if (session.getAttribute("spectrumList") != null)
            return "submission";

        return "upload";
    }

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        if (request.getSession().getAttribute("spectrumList") != null) {
//            request.getRequestDispatcher("/WEB-INF/jsp/view/submission.jsp")
//                    .forward(request, response);
//            return;
//        }
//
//        request.getRequestDispatcher("/WEB-INF/jsp/view/upload.jsp")
//                .forward(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        Part filePart = request.getPart("file1");
//        if (filePart == null || filePart.getSize() == 0) {
//            request.setAttribute("emptyFile", false);
//            request.getRequestDispatcher("/WEB-INF/jsp/view/upload.jsp")
//                    .forward(request, response);
//            return;
//        }
//
//        List<Spectrum> spectra = MspReader.read(filePart.getInputStream());
//        if (spectra == null || spectra.isEmpty()) {
//            request.setAttribute("emptyFile", false);
//            request.getRequestDispatcher("/WEB-INF/jsp/view/upload.jsp")
//                    .forward(request, response);
//            return;
//        }
//
//        request.getSession().setAttribute("spectrumList", spectra);
//        request.getSession().setAttribute("fileName", filePart.getSubmittedFileName());
//        request.getRequestDispatcher("/WEB-INF/jsp/view/submission.jsp")
//                .forward(request, response);
//    }
}
