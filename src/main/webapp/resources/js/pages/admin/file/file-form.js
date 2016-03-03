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
        labels: {
            pagination: "Pagination",
            finish: "Сохранить",
            next: "Далее",
            previous: "Назад",
            loading: "Загрузка ..."
        }
    });

    Dropzone.options.myAwesomeDropzone = {
        //paramName: "file",
        autoProcessQueue: false,
        uploadMultiple: true,
        parallelUploads: 10,
        maxFiles: 10,
        maxFilesize: 5120, // MB
        // Dropzone settings
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
                response.success.forEach(function(item, i){
                    filesInfo.push(item);
                });
                if (errorCount < files.length) {
                    // show next btn
                }
            });
            this.on("errormultiple", function(files, response) {
                console.log("err mult");
            });
        }
    };
});


var lineTemplate = '<div class="form-group">\
                        <label>{title}</label>\
                        <input type="text" class="form-control" data-fileid="{fileId}" data-propertyid="{propertyId}">\
                    </div>';
function loadFinishStepContent() {
    var secondStep = $('#secondStep');
    var markup = '';
    filesInfo.forEach(function(item){
        console.log(item);
        var str = '<h3>Файл: '+item.fileVersionName+'</h3>';
        var line = lineTemplate;
        str += line.replace('{title}', 'Название продукта').replace('{fileVersionId}', item.fileVersionId).replace('{propertyId}', 9);
        line = lineTemplate;
        str += line.replace('{title}', 'Версия файла').replace('{fileVersionId}', item.fileVersionId).replace('{propertyId}', 3);
        str += '<hr>';
        markup += str;
    });
    secondStep.html(markup);
}