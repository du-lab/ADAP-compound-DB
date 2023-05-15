package org.dulab.adapcompounddb.site.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.GoogleResponse;
import org.dulab.adapcompounddb.site.services.io.ExcelExportSearchResultsService;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaService.class);
    private final RestOperations restTemplate = new RestTemplate();
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
    public static String GOOGLE_CAPTCHA_RESPONSE = "g-recaptcha-response";

    private boolean INTEGRATION_TEST = System.getenv("INTEGRATION_TEST") == null ? false
        : Boolean.parseBoolean(System.getenv("INTEGRATION_TEST"));

    public void processResponse(String response, String ip) {
        final boolean disableCaptcha = INTEGRATION_TEST;
        if (!disableCaptcha) {
            if (!responseSanityCheck(response)) {
                LOGGER.error("Google response contains invalid characters");
                throw new IllegalStateException("Response contains invalid characters");
            }
            URI verifyUri = URI.create(String.format(
                    "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                    System.getenv("CAPTCHA_SECRET_KEY"), response, ip));

            GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

            if (googleResponse == null)
                throw new IllegalStateException("reCaptcha was not successfully validated");

            if (!googleResponse.isSuccess()) {
                GoogleResponse.ErrorCode[] errorCodes = googleResponse.getErrorCodes();
                String[] arrStr = Arrays.stream(errorCodes)
                        .map(Enum::toString)
                        .toArray(String[]::new);
                LOGGER.error("Google captcha errors: " + String.join(",", arrStr));

                throw new IllegalStateException("reCaptcha was not successfully validated");
            }
        }
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

}
