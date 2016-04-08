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

    $('#deleteVersionBtn').on('click', function(){
        var $this = $(this);
        if (confirm("Вы действительно хотите удалить версию?")) {
            $.ajax({
                url: fileVersionDeleteUrl,
                method: "post",
                dataType: "json",
                data: {versionId: $this.data('versionid')},
                success: function (data) {
                    if (!data.error) {
                        toastr.success('Файл помечен как удаленный');
                    } else {
                        toastr.error('Ошибка при удалении');
                    }
                },
                error: function (data) {
                    toastr.error('Ошибка при удалении', data.msg);
                }
            });
        }
        return false;
    });

    $('#deleteVersionPermanentBtn').on('click', function(){
        var $this = $(this);
        if (confirm("Вы действительно хотите окончательно удалить файл?")) {
            $.ajax({
                url: fileVersionDeletePermanentUrl,
                method: "post",
                dataType: "json",
                data: {versionId: $this.data('versionid')},
                success: function (data) {
                    if (!data.error) {
                        toastr.success('Файл удален');
                    } else {
                        toastr.error('Ошибка при удалении');
                    }
                },
                error: function (data) {
                    toastr.error('Ошибка при удалении', data.msg);
                }
            });
        }
        return false;
    });

    $('#recoverVersionBtn').on('click', function(){
        var $this = $(this);
        if (confirm("Восстановить файл?")) {
            $.ajax({
                url: fileVersionRecoverUrl,
                method: "post",
                dataType: "json",
                data: {versionId: $this.data('versionid')},
                success: function (data) {
                    if (!data.error) {
                        toastr.success('Файл восстановлен');
                    } else {
                        toastr.error('Ошибка при восстановлении');
                    }
                },
                error: function (data) {
                    toastr.error('Ошибка при восстановлении', data.msg);
                }
            });
        }
        return false;
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
