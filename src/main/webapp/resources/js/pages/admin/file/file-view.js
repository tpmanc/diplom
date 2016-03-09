$(function () {
    $('.remove-file-property').on('click', function () {
        var $this = $(this);
        if (confirm("Удалить свойство?")) {
            $.ajax({
                url: filePropertyDeleteUrl,
                method: "post",
                dataType: "json",
                data: {propertyLink: $this.data('link')},
                success: function (data) {
                    if (!data.error) {
                        $this.closest('.file-property-holder').remove();
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
