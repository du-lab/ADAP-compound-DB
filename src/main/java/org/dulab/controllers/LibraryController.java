package org.dulab.controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet (
        name = "libraryServlet",
        urlPatterns = "/library"
)
public class LibraryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/jsp/view/library.jsp")
                .forward(request, response);
    }
}
