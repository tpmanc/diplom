var filesInfo = [];
var steps;
var secondStep;

$(function () {
    "use strict";

    secondStep = $('#secondStep');
    steps = $('#steps');
    steps.steps({
        headerTag: "h2",
        bodyTag: ".step-content",
        transitionEffect: "slide",
        enableContentCache: false,
        enableCancelButton: false,
        enableFinishButton: true,
        onStepChanging: function (event, currentIndex, newIndex) {
            var finishBtn = steps.find('a[href="#finish"]');
            if (newIndex == 1) {
                finishBtn.show();
                loadFinishStepContent();
            } else {
                finishBtn.hide();
            }
            return true;
        },
        onFinishing: function () {
            return false;
        },
        labels: {
            pagination: "Pagination",
            finish: "Сохранить",
            next: "Далее",
            previous: "Назад",
            loading: "Загрузка ..."
        }
    });

    steps.find('a[href="#finish"]').hide();
    steps.find('ul[role="menu"]').hide();
    steps.find('a[href="#finish"]').on('click', function(){
        var res = [];
        var files = steps.find('.file-holder');
        steps.find('.has-error').removeClass('has-error');
        files.each(function(i, e){
            var $e = $(e);
            var versionId = $e.data('versionid');
            var title = $e.find('.title-autocomplete');
            var version = $e.find('.version-autocomplete');
            var titleVal = title.val().trim();
            var versionVal = version.val().trim();
            var error = false;
            if (titleVal.length == 0) {
                error = true;
                title.closest('.title-block').addClass('has-error').find('.help-block').text('Заполните название продукта');
            }
            if (versionVal.length == 0) {
                error = true;
                version.closest('.version-block').addClass('has-error').find('.help-block').text('Заполните версию файла');
            }
            if (!error) {
                var file = {"id": versionId, "title": titleVal, "version": versionVal};
                res.push(file);
            }
        });

        // если хоть 1 файл заполнен
        if (res.length > 0) {
            $.ajax({
                url: fileFillingUrl,
                method: "post",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                dataType: "json",
                data: JSON.stringify(res),
                success: function (data) {
                    steps.find('.has-error').removeClass('has-error');
                    var success = data.success;
                    $(success).each(function (i, e) {
                        files.each(function (index, elem) {
                            var $elem = $(elem);
                            if ($elem.data('versionid') == e.id) {
                                $elem.closest('.file-holder').remove();
                            }
                        });
                    });
                    var errors = data.errors;
                    $(errors).each(function (i, e) {
                        files.each(function (index, elem) {
                            var $elem = $(elem);
                            if ($elem.data('versionid') == e.id) {
                                if (e.msgTitle != undefined) {
                                    $elem.find('.title-block').addClass('has-error').find('.help-block').text(e.msgTitle);
                                }
                                if (e.msgVersion != undefined) {
                                    $elem.find('.version-block').addClass('has-error').find('.help-block').text(e.msgVersion);
                                }
                            }
                        });
                    });
                },
                error: function (data) {
                    toastr.error('Ошибка при сохранении', data.msg);
                },
                complete: function () {
                }
            });
        }
    });

    Dropzone.options.myAwesomeDropzone = {
        //paramName: "file",
        autoProcessQueue: false,
        uploadMultiple: true,
        parallelUploads: 10,
        maxFiles: 10,
        maxFilesize: 5120, // MB
        init: function() {
            var myDropzone = this;

            this.element.querySelector("button[type=submit]").addEventListener("click", function(e) {
                e.preventDefault();
                e.stopPropagation();
                myDropzone.processQueue();
            });
            this.on("sendingmultiple", function() {
            });
            this.on("successmultiple", function(files, response) {
                console.log(files);
                console.log(response);
                var errorCount = 0;
                response.errors.forEach(function(item, i){
                    var elem = $(files[item.number].previewElement);
                    elem.removeClass('dz-success').addClass('dz-error');
                    elem.find('.dz-error-message').text(item.msg);
                    errorCount++;
                });
                var needNextBtn = false;
                response.success.forEach(function(item, i){
                    filesInfo.push(item);
                    if (!item.isFilled) {
                        needNextBtn = true;
                    }

                });
                if (errorCount < files.length) {
                    // показываем кнопку далее если хоть 1 файл не заполнен
                    if (needNextBtn) {
                        steps.find('ul[role="menu"]').show();
                    }
                }
            });
            this.on("errormultiple", function(files, response) {
                console.log("err mult");
            });
        }
    };
});

var fileTemplate = '<div class="file-holder" data-versionid="{fileVersionId}">\
                        <h3>Файл: {fileName}</h3>\
                        <div class="form-group title-block">\
                            <label>Название продукта</label>\
                            <input type="text" class="form-control title-autocomplete">\
                            <span class="help-block"></span>\
                        </div>\
                        <div class="form-group version-block">\
                            <label>Версия файла</label>\
                            <input type="text" class="form-control version-autocomplete">\
                            <span class="help-block"></span>\
                        </div>\
                    </div><hr>';
function loadFinishStepContent() {
    var secondStep = $('#secondStep');
    var markup = '';
    filesInfo.forEach(function(item){
        console.log(item);
        // если файл не заполнен, то добавляем поля для заполнения
        if (!item.isFilled) {
            var str = fileTemplate;
            str = str.replace('{fileVersionId}', item.fileVersionId).replace('{fileName}', item.fileVersionName);
            markup += str;
        }
    });
    secondStep.html(markup);
    secondStep.find('.title-autocomplete').autocomplete('dispose').autocomplete({
        serviceUrl: fileTitleUrl,
        onSelect: function (suggestion) {

        }
    });
}