document.addEventListener('DOMContentLoaded', function(){
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
}, false);