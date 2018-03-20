package org.dulab.controllers;

import org.dulab.models.Spectrum;
import org.dulab.models.readers.MspReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet (
        name = "libraryServlet",
        urlPatterns = {"/library"}
)
@MultipartConfig (
        fileSizeThreshold = 5242880,  // 5MB
        maxFileSize = 20971520L,  // 20MB
        maxRequestSize = 41943040L  // 40MB
)
public class LibraryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null)
            action = "view";

        switch (action) {
            case "uploadspectrum":
                request.getRequestDispatcher("/WEB-INF/jsp/view/fileupload.jsp")
                        .forward(request, response);
                break;
            case "view":
            default:
                library(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null)
            action = "view";

        switch (action) {
            case "upload":
                uploadSpectrum(request, response);
                break;
            case "view":
            default:
                response.sendRedirect("library");

        }
    }

    private void uploadSpectrum(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Spectrum> spectra = null;
        Part filePart = request.getPart("file1");
        if (filePart != null && filePart.getSize() > 0) {
            spectra = MspReader.read(filePart.getInputStream());
        }

        if (spectra == null || spectra.isEmpty()) {
            request.setAttribute("emptyFile", false);
            request.getRequestDispatcher("/WEB-INF/jsp/view/fileupload.jsp")
                    .forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("spectrumList", spectra);
        request.getRequestDispatcher("/WEB-INF/jsp/view/fileview.jsp")
                .forward(request, response);
    }

    private void library(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/jsp/view/library.jsp")
                .forward(request, response);
    }
}
