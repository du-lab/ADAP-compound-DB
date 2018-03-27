package org.dulab.controllers;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(
        name = "submissionController",
        urlPatterns = {"/submission"}
)
public class SubmissionController extends HttpServlet {
}
