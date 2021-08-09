function ProgressBar(url, progressBarId, interval, callback) {

    var progress = $('#' + progressBarId);

    this.start = function () {
        var width = 0;

        var id = setInterval(frame, interval);
        var active = false;

        function frame() {
            if (width >= 100) {
            	console.log("complete");
                complete();
            } else {
                $.getJSON(window.location.href.concat(url), function(percentage) {
                    width = percentage;
                    if(width < 0) {
                        complete();
                    } else {
                        $(progress).removeClass("hide");
                        $(progress).attr("value", width);
                        active = true;
                    }
                });

            }
        };

        function complete() {
            clearInterval(id);
            $(progress).addClass("hide");
            callback();
            if(active) {
            	window.location.href=window.location.href;
            }
        }

        frame();
    };
}