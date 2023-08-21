(function ($) {
    $.fn.confirmDialog = function (buttonName = 'Confirm') {

        var div = $(this);
        //creating the pop up dialog
        div.attr('title', 'Confirm');
        div.html('<p></p>');

        div.dialog({
            autoOpen: false,
            resizable: false,
            height: 'auto',
            width: 400,
            modal: true,
            buttons: {
                [buttonName]: function () {
                    div.find('p').html('');
                    $('.ui-dialog-title').html('In Progress...')
                    $('.ui-dialog-buttonset').children().hide()

                    window.location.replace($(this).attr('href'));
                },
                'Cancel': function () {
                    $(this).dialog('close');
                }
            }
        });

        this.show = function(message, url) {
            var div = $(this);
            div.find('p').html(message);
            div.attr('href', url);
            div.dialog('open');
        };

        return this;
    }
})(jQuery);

(function ($) {
    $.fn.progressDialog = function () {

        var div = $(this);

        div.attr('title', 'In progress...');
        div.append('<p></p>');

        div.dialog({
            autoOpen: false,
            resizable: false,
            height: 'auto',
            width: 400,
            modal: true
        });

        this.show = function(message) {
            var div = $(this);
            div.find('p').html(message);
            div.dialog('open');
        };

        return this;
    }
})(jQuery);
