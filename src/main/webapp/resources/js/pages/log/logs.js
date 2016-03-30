$(function(){
    $('#levelSelect').on('change', function(){
        $(this).closest('form').submit();
    });

    $('#clearLogsBtn').on('click', function(){
        if (confirm('Вы действительно хотите очистить логи?')) {
            return true;
        }
        return false;
    });
});