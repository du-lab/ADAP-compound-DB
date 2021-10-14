jQuery.fn.spectrumStructure = function (restURL, onComplete) {
    let div = $(this);
    $.ajax({
        dataType: 'json',
        url: restURL,
        complete: function (data) {

            console.log(data.responseJSON)

            const jsonInfo = data.responseJSON
            if (jsonInfo === undefined || jQuery.isEmptyObject(jsonInfo)){
                onComplete(false);
                return;
            }

            // Remove all child elements
            div.text(null);
            while (div.firstChild)
                div.firstChild.remove();


            div.html(jsonInfo["image"]);

            onComplete(true);

        }
    });
}