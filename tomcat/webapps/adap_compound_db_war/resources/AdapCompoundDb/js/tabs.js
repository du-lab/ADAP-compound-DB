(function ($) {
    $.fn.tabbedPane = function (id) {

        var pane = $(this);

        tabs = pane.children();
        numberOfTabs = $(tabs).length;
        if(numberOfTabs > 1) {
        	tabs.each(function(index, element) {
	            if(index < numberOfTabs - 1) {
	                $(element).css("border-right", "3px solid #ffffff");
	            }

	            if(index === 0) {
	                $(element).css("border-radius", "10px 0 0 0");
	            }

	            if(index === numberOfTabs - 1) {
	            	$(element).css("border-radius", "0 10px 0 0");
	            }
            	$(element).css("width", "calc(" + 100.0/(numberOfTabs) + "% - 7px");

                $(element).click(function() {
                    $(this).parent().children().each(function() {
                        $(this).removeClass("active");
                        $("#" + $(this).data("tab")).addClass("hide");
                    });
                    $(this).addClass("active");
                    $("#" + $(this).data("tab")).removeClass("hide");
                    $( id ).DataTable().columns.adjust().draw();
                });
	        });
        }

        return this;
    }
})(jQuery);