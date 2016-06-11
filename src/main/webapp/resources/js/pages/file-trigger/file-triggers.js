$(function(){
    $('.remove-file-trigger').on('click', function () {
        var $this = $(this);
        if (confirm("Удалить триггер?")) {
            $.ajax({
                url: fileTriggerDeleteUrl,
                method: "post",
                dataType: "json",
                data: {id: $this.data('id')},
                success: function (data) {
                    if (!data.error) {
                        $this.closest('tr').remove();
                        toastr.success('Триггер удален');
                    } else {
                        toastr.error('Ошибка при удалении');
                    }
                },
                error: function (data) {
                    toastr.error('Ошибка при удалении', data.msg);
                }
            });
        }
    });
});