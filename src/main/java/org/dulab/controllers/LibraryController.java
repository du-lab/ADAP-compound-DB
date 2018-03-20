package org.dulab.controllers;

import org.dulab.models.readers.MspReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;

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
                request.getRequestDispatcher("/WEB-INF/jsp/view/uploadspectrum.jsp")
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

        Part filePart = request.getPart("file1");
        if (filePart != null && filePart.getSize() > 0) {
            MspReader.read(filePart.getInputStream());
        }

    }

    private void library(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/jsp/view/library.jsp")
                .forward(request, response);
    }
}
