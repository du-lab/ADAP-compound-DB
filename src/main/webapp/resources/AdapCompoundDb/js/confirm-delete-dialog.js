(function ($) {
    $.fn.confirmDeleteDialog = function () {

        $(this).append('<p>Delete "' + $(this).attr('data-name') + '"?</p>');

        $(this).dialog({
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

        this.show = function() {
            console.log('Show!');
        };

        return this;
    }
})(jQuery);