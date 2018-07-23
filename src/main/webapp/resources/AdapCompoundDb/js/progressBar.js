function ProgressBar(divId) {

    var div = $('#' + divId);
    // div.prop('hidden', 'true');

    this.start = function (url) {
        var width = 0;
        var id = setInterval(frame, 1000);
        // div.prop('hidden', 'false');

        function frame() {
            if (width >= 100) {
                clearInterval(id);
                div.css('width', '100%');
                // div.prop('hidden', 'true');
            }
            else {
                $.getJSON(window.location.href.concat(url), function(percentage) {width = percentage;});
                div.css('width', width + '%');
            }
        }
    };
}