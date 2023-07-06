$(document).ready(function () {
    $('#submission-select').change(function () {
        let selectedValue = $(this).val();
        if (selectedValue.includes('0')) {
            $('#species-container').show();
        } else {
            $('#species-container').hide();
        }
    });
});