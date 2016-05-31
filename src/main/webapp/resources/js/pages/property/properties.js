$(function(){
    $('.remove-property').on('click', function () {
        var $this = $(this);
        if (confirm("Удалить свойство?")) {
            $.ajax({
                url: propertyDeleteUrl,
                method: "post",
                dataType: "json",
                data: {id: $this.data('id')},
                success: function (data) {
                    if (!data.error) {
                        $this.closest('tr').remove();
                        toastr.success('Свойство удалено');
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