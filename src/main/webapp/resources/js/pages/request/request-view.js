$(function(){
    $('#requestDeleteBtn').on('click', function(){
        if (confirm('Удалить заявку?')) {
            return true;
        }
        return false;
    });
});