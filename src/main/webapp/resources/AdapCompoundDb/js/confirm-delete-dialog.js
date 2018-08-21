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
            div.find('p').html('User "' + message + '" and all user\'s submissions will be deleted. Are you sure?');
            div.attr('href', url);
            div.dialog('open');
        };

        return this;
    }
})(jQuery);