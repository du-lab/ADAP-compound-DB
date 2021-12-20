package org.dulab.adapcompounddb.site.controllers.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.entities.Peak;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ConversionsUtils {

    private static final Logger LOGGER = LogManager.getLogger(ConversionsUtils.class);


    public static String peaksToJson(Collection<Peak> peaks) {
        return String.format("[%s]", peaks.stream()
                .map(p -> String.format("{'mz':%f,'intensity':%f}", p.getMz(), p.getIntensity()))
                .collect(Collectors.joining(",")));
    }

    public static List<Peak> jsonToPeaks(String json) {
        JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
        List<Peak> peaks = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); ++i) {
            Peak peak = new Peak();
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            peak.setMz(jsonObject.get("mz").getAsDouble());
            peak.setIntensity(jsonObject.get("intensity").getAsDouble());
            peaks.add(peak);
        }
        return peaks;
    }

    public static String formatDouble(Double x) {
        if (x == null) return null;
        return String.format("%.3f", x);
    }

    public static String smilesToImage(@Nullable String smiles) {

        if (smiles == null || smiles.isEmpty())
            return null;

        try {
            // using the Runtime exec method:
            String command = String.format("python3 generate_image_for_smiles.py %s", smiles);

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder output = new StringBuilder();

            String image;
            String read;
            while ((read = stdInput.readLine()) != null) {
                output.append(read);
            }
            image = output.toString();

            if (!image.isEmpty())
                return image;

            // read any errors from the attempted command
            String s = null;
            while ((s = stdError.readLine()) != null) {
                LOGGER.warn(s);
                LOGGER.warn("Working directory: " + System.getProperty("user.dir"));
            }

        } catch (IOException e) {
            LOGGER.warn("Error while plotting a structure for SMILES", e);
        }
        return null;
    }

    public static <T> T byteStringToForm(String jsonString, Class<T> formClass) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            byte[] jsonBytes = Base64.getDecoder().decode(jsonString);
            return objectMapper.readValue(jsonBytes, formClass);
        } catch (IOException e) {
            try {
                return formClass.getConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                throw new IllegalStateException("Cannot initialize a form: " + ex.getMessage(), ex);
            }
        }
    }

    public static <T> String formToByteString(T form) {

        ObjectMapper objectMapper = new ObjectMapper();

        byte[] jsonBytes;
        try {
            jsonBytes = objectMapper.writeValueAsBytes(form);
            return Base64.getEncoder().encodeToString(jsonBytes);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Cannot convert Form to Json: " + e.getMessage(), e);
            return "";
        }
    }
}
