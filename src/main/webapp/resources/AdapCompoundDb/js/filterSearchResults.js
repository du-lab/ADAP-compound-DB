$(document).ready(function () {
    $("#species-container").hide();
    $('#submission-select').change(function () {
        let selectedValue = $(this).val();
        if (selectedValue.includes('0')) {
            $('#species-container').show();
        } else {
            $('#species-container').hide();
        }
    });
    let subElement = $('#submissionIds0')[0];
    $(subElement).on('change', function() {
        if (this.checked) {
            $("#filterTab").show()
        } else {
            $("#filterTab").hide()
        }
    });
});