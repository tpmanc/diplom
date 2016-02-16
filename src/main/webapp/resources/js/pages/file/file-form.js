$(function () {
    $('#fileUploadForm').dropzone({
        paramName: "file", // The name that will be used to transfer the file
        maxFilesize: 4048, // MB
    });
});