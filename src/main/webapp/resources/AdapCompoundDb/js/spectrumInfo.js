jQuery.fn.spectrumInfo = function (restURL) {
    let div = $(this);

    // let href = urlPrefix + (spectrumId > 0
    //     ? `/spectrum/${spectrumId}/search/info.json`
    //     : `/file/${fileIndex}/${spectrumIndex}/search/ingo.json`);

    $.ajax({
        dataType: 'json',
        url: restURL,
        complete: function (data) {
            const jsonInfo = data.responseJSON

            // Remove all child elements
            div.text(null);
            while (div.firstChild)
                div.firstChild.remove();

            if (!jsonInfo) return;

            div.append($('<strong/>').text(jsonInfo['name']));
            div.append('&nbsp;');
            div.append($('<span/>').attr('class', 'badge badge-info').text(jsonInfo['chromatographyType']));
            div.append('<br/>');

            // Standard properties
            div.append($('<strong/>').text('Standard properties'));
            const standardProperties = $('<ul/>').attr('class', 'list-group list-group-flush');
            jsonInfo['standardProperties'].forEach(function (p) {
                standardProperties.append($('<li/>')
                    .attr('class', 'list-group-item py-0')
                    .append($('<strong>/').text(p['name']))
                    .append(':&nbsp;')
                    .append($('<span/>').attr('style', 'word-break: break-all').text(p['value'])));
            });
            div.append(standardProperties);

            // Other properties
            div.append($('<strong/>').text('Other properties'));
            const otherProperties = $('<ul/>').attr('class', 'list-group list-group-flush');
            jsonInfo['otherProperties'].forEach(function (p) {
                otherProperties.append($('<li/>')
                    .attr('class', 'list-group-item py-0')
                    .append($('<strong>/').text(p['name']))
                    .append(':&nbsp;')
                    .append($('<span/>').attr('style', 'word-break: break-all').text(p['value'])));
            });
            div.append(otherProperties);
        }
    });
}