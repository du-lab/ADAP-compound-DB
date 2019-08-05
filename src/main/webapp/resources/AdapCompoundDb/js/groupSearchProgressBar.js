function groupSearchProgressBar(url, progressBarId, interval) {

    var progress = $( '#' + progressBarId );

    this.start = function () {
        var width = 0;
        var id = setInterval( frame, interval );
        var active = false;

        frame();

        function frame() {
            if (width >= 100) {
                console.log( "complete" );
                clearInterval( id );
                $( progress ).attr( "value", width );
            } else {
                $.getJSON( window.location.href.concat( url ), function (percentage) {
                    width = percentage;
                    if (width < 0) {
                        clearInterval( id );
                        $( progress ).attr( "value", width );
                        $( progress ).addClass( "hide" );
                    } else {
                        $( progress ).removeClass( "hide" );
                        $( progress ).attr( "value", width );
                        active = true;
                    }
                } );
            }
        };
    };
}