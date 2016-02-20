$(function () {
    $(document).ready(function(){

        "use strict";

        Dropzone.options.myAwesomeDropzone = {
            paramName: "file",
            autoProcessQueue: false,
            uploadMultiple: false,
            parallelUploads: 100,
            maxFiles: 100,
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
                });
                this.on("errormultiple", function(files, response) {
                });
            }
        };
    });
});