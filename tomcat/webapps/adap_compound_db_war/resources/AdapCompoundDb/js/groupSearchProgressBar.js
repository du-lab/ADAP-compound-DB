function GroupSearchProgressBar(url, progressBarId, interval) {

    let progress = $('#' + progressBarId);

    this.start = function () {
        let width = 0;
        let id = setInterval(frame, interval);

        frame();

        function frame() {
            if (width >= 100) {
                clearInterval(id);
                $(progress).attr("value", width);
            } else {
                $.getJSON(url, function (percentage) {
                    width = percentage;
                    if (width < 0) {
                        clearInterval(id);
                        $(progress).attr("value", width);
                        // $(progress).addClass("hide");
                    } else if (width === 0) {
                        $(progress).attr("value", width);
                        // $(progress).addClass("hide");
                    } else {
                        // $(progress).removeClass("hide");
                        $(progress).attr("value", width);
                    }
                });
            }
        }
    };
}