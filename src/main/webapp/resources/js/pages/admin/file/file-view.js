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

    $('.remove-file-version-property').on('click', function () {
        var $this = $(this);
        if (confirm("Удалить свойство?")) {
            $.ajax({
                url: fileVersionPropertyDeleteUrl,
                method: "post",
                dataType: "json",
                data: {propertyLink: $this.data('link')},
                success: function (data) {
                    if (!data.error) {
                        $this.closest('.file-version-property').remove();
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

    $('#versionSelect').on('change', function(){
        $(this).closest('form').submit();
    });
});
