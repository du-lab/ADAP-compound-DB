package org.dulab.controllers;

import org.dulab.models.Spectrum;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "spectrumServlet",
        urlPatterns = {"/library/submission/spectrum"}
)
public class SpectrumController extends HttpServlet {

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Spectrum> spectrumList = (List<Spectrum>) request.getSession().getAttribute("spectrumList");
        if (spectrumList == null) {
            request.getRequestDispatcher("/WEB-INF/jsp/view/fileview.jsp");
            return;
        }

        Spectrum spectrum = null;
        try {
            int index = Integer.parseInt(request.getParameter("spectrumIndex"));
            spectrum = spectrumList.get(index);
        }
        catch (NumberFormatException | NullPointerException | IndexOutOfBoundsException e) {
            request.getRequestDispatcher("/WEB-INF/jsp/view/fileview.jsp");
            return;
        }

        if (spectrum == null) {
            request.getRequestDispatcher("/WEB-INF/jsp/view/fileview.jsp");
            return;
        }

        request.setAttribute("name", spectrum.toString());
        request.setAttribute("properties", spectrum.getProperties());

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

        request.setAttribute("jsonPeaks", stringBuilder.toString());

        request.getRequestDispatcher("/WEB-INF/jsp/view/spectrumview.jsp")
                .forward(request, response);
    }
}
