function ProgressBar(url, progressBarId, interval, callback) {

    var progress = $('#' + progressBarId);

    this.start = function () {
        var width = 0;

        var id = setInterval(frame, interval);

        function frame() {
            if (width >= 100) {
                console.log("width1: " + width);
                clearInterval(id);
                $(progress).addClass("hide");
                callback();
            } else {
                $.getJSON(window.location.href.concat(url), function(percentage) {
                    width = percentage;
                    console.log("width2: " + width + " " + percentage);
                    if(width < 0) {
                        clearInterval(id);
                        $(progress).addClass("hide");
                        callback();
                    } else {
                        $(progress).removeClass("hide");
                        $(progress).attr("value", width);
                    }
                });

            }
        };

        frame();
    };
}