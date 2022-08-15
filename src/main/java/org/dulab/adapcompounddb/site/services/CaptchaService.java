package org.dulab.adapcompounddb.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.GoogleResponse;
import org.dulab.adapcompounddb.site.services.io.ExcelExportSearchResultsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.print.DocFlavor;
import java.net.URI;
import java.util.Arrays;
import java.util.regex.Pattern;

@Service
public class CaptchaService {

    private static final Logger LOGGER = LogManager.getLogger(CaptchaService.class);
    private RestOperations restTemplate = new RestTemplate();

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    public static String GOOGLE_CAPTCHA_RESPONSE = "g-recaptcha-response";

    public void processResponse(String response, String ip) {
        if(!responseSanityCheck(response)) {
            LOGGER.error("Google response contains invalid characters");
            throw new IllegalStateException("Response contains invalid characters");
        }
        URI verifyUri = URI.create(String.format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                System.getenv("CAPTCHA_SECRET_KEY"), response, ip));

        GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

        if(!googleResponse.isSuccess()) {
            String errors = "";
            GoogleResponse.ErrorCode[] errorCodes = googleResponse.getErrorCodes();
            String[] arrStr = Arrays.stream(errorCodes)
                    .map(e -> e.toString())
                    .toArray(String[]::new);
            LOGGER.error("Google captcha errors: " + String.join(",", arrStr));

            throw new IllegalStateException("reCaptcha was not successfully validated");
        }
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

}
