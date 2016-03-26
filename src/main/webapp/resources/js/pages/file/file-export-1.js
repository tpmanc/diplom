$(function () {
    var parameters = $('#parameters');
    var parametersBody = parameters.find('tbody');

    parameters.find('.add-parameter').on('click', function(){
        parametersBody.append(lineTemplate);
    });
    parameters.on('click', '.param-delete', function(){
        $(this).closest('tr').remove();
    });
});
