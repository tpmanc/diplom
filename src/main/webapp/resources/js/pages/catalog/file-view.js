$(function(){
    $('#versionSelect').on('change', function(){
        $(this).closest('form').submit();
    });
});