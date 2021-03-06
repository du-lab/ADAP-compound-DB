(function ($) {
    $.fn.confirmDeleteDialog = function () {

        var div = $(this);

        div.attr('title', 'Confirm');
        div.append('<p></p>');

        div.dialog({
            autoOpen: false,
            resizable: false,
            height: 'auto',
            width: 400,
            modal: true,
            buttons: {
                'Delete': function () {
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
