const logging = {
    logToServer: function (url, message) {
        let logEventObject = {
            "message": message,
            "location": location.href,
            "browser": navigator.userAgent
        };

        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(logEventObject),
            contentType: "application/json",
            cache: "false"
        });
    }
}