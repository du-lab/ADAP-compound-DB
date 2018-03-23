package org.dulab.controllers;

import org.dulab.models.Spectrum;
import org.dulab.models.readers.MspReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "SubmissionController",
        urlPatterns = {"/library/submission"}
)
@MultipartConfig(
        fileSizeThreshold = 5242880,  // 5MB
        maxFileSize = 20971520L,  // 20MB
        maxRequestSize = 41943040L  // 40MB
)
public class SubmissionController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getSession().getAttribute("spectrumList") != null) {
            request.getRequestDispatcher("/WEB-INF/jsp/view/submission.jsp")
                    .forward(request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/view/upload.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Part filePart = request.getPart("file1");
        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("emptyFile", false);
            request.getRequestDispatcher("/WEB-INF/jsp/view/upload.jsp")
                    .forward(request, response);
            return;
        }

        List<Spectrum> spectra = MspReader.read(filePart.getInputStream());
        if (spectra == null || spectra.isEmpty()) {
            request.setAttribute("emptyFile", false);
            request.getRequestDispatcher("/WEB-INF/jsp/view/upload.jsp")
                    .forward(request, response);
            return;
        }

        request.getSession().setAttribute("spectrumList", spectra);
        request.getSession().setAttribute("fileName", filePart.getSubmittedFileName());
        request.getRequestDispatcher("/WEB-INF/jsp/view/submission.jsp")
                .forward(request, response);
    }
}
