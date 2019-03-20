package org.dulab.adapcompounddb.site.services;

import javax.validation.Valid;

import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.FeedbackDTO;
import org.dulab.adapcompounddb.models.entities.Feedback;

public interface FeedbackService {

    void saveFeedback(@Valid FeedbackDTO form);

    void sendFeedbackEmail(Feedback feedback);

    DataTableResponse findAllFeedbackForResponse(String searchStr, Integer start, Integer length, Integer column,
            String sortDirection);

    FeedbackDTO getFeedBackById(Integer id);

    void markRead(Integer id);
    String htmlTemplate = "<html lang=\"en\" class=\"\">" +
            "" +
            "<head>" +
            "" +
            "    <style class=\"cp-pen-styles\">.row {" +
            "      background: #f8f9fa;" +
            "      margin-top: 20px;" +
            "    }" +
            "" +
            "    .content {" +
            "      border: solid 1px #6c757d;" +
            "      padding: 10px;" +
            "    }" +
            "" +
            "    .container {" +
            "      width: 40%;" +
            "      padding: 10px;" +
            "      border: solid 1px #6c757d;" +
            "      background-color: #49d889;" +
            "    }" +
            "" +
            "    .message {" +
            "      border: solid 1px #6c757d;" +
            "      padding: 10px;" +
            "      background: #b67960;" +
            "      font-weight: bold;" +
            "    }" +
            "    </style>" +
            "</head>" +
            "<body>" +
            "<!-- " +
            "  Bootstrap docs: https://getbootstrap.com/docs" +
            "-->" +
            "" +
            "    <div class=\"container\">" +
            "      <div class=\"row\">" +
            "        <div class=\"col\">" +
            "          Name" +
            "        </div>" +
            "        <div class=\"col content\">" +
            "          {name}" +
            "        </div>" +
            "      </div>" +
            "" +
            "      <div class=\"row\">" +
            "        <div class=\"col\">" +
            "          Email" +
            "        </div>" +
            "        <div class=\"col content\">" +
            "          {email}" +
            "        </div>" +
            "      </div>" +
            "" +
            "      <div class=\"row\">" +
            "        <div class=\"col\">" +
            "          Affiliation" +
            "        </div>" +
            "        <div class=\"col content\">" +
            "          {affiliation}" +
            "        </div>" +
            "      </div>" +
            "" +
            "      <div class=\"row\">" +
            "        <div class=\"col\">" +
            "          Message" +
            "        </div>" +
            "      </div>" +
            "" +
            "      <div class=\"row\">" +
            "        <div class=\"col message\">" +
            "          {message}" +
            "        </div>" +
            "      </div>" +
            "    </div>" +
            "" +
            "</body>" +
            "</html>";
}
