$(function(){
    $('#titleAutocomplete').autocomplete({
        serviceUrl: fileTitleUrl,
        onSelect: function (suggestion) {
        }
    });
});