package org.dulab.adapcompounddb.site.controllers.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtils {

    private static final Pattern FILE_URL_PATTERN = Pattern.compile("/file/([0-9]+)/([0-9]+)/");

    public static Integer getFileIndexFromURL(String urlString) {
        Matcher matcher = FILE_URL_PATTERN.matcher(urlString);
        if (matcher.find())
            return Integer.parseInt(matcher.group(1));
        return null;
    }

    public static Integer getSpectrumIndexFromURL(String urlString) {
        Matcher matcher = FILE_URL_PATTERN.matcher(urlString);
        if (matcher.find())
            return Integer.parseInt(matcher.group(2));
        return null;
    }
}
