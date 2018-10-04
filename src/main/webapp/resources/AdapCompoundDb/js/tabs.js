/*document.addEventListener('DOMContentLoaded', function(){
    $(".tabbed-pane span").each(function() {
        $(this).click(function() {
            $(this).parent().children().each(function() {
                $(this).removeClass("active");
                $("#" + $(this).data("tab")).addClass("hide");
            });
            $(this).addClass("active");
            $("#" + $(this).data("tab")).removeClass("hide");
        });
    });
}, false);*/

(function ($) {
    $.fn.tabbedPane = function () {

        var pane = $(this);

        tabs = pane.children();
        numberOfTabs = $(tabs).length;
        if(numberOfTabs > 1) {
        	tabs.each(function(index, element) {
	            if(index < numberOfTabs - 1) {
	                $(element).css("border-right", "3px solid #ffffff");
	                //$(element).css("box-shadow", "5px 0px 0px 0px #ffffff");
	                $(element).css("margin-right", "-3px");
	            	$(element).css("width", 100.0/(numberOfTabs) + "%");
	            }

	            if(index === 0) {
	                $(element).css("border-radius", "10px 0 0 0");
	            }

	            if(index === numberOfTabs - 1) {
	            	$(element).css("border-radius", "0 10px 0 0");
	            	$(element).css("width", (100.0/(numberOfTabs) - 0.5).toFixed(1) + "%");
	            }

                $(element).click(function() {
                    $(this).parent().children().each(function() {
                        $(this).removeClass("active");
                        $("#" + $(this).data("tab")).addClass("hide");
                    });
                    $(this).addClass("active");
                    $("#" + $(this).data("tab")).removeClass("hide");
                });
	        });
        }

        return this;
    }
})(jQuery);