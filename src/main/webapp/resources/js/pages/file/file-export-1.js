$(function () {
    var parameters = $('#parameters');
    var parametersBody = parameters.find('tbody');

    parameters.find('.add-parameter').on('click', function(){
        parametersBody.append(lineTemplate);
    });
    parameters.on('click', '.param-delete', function(){
        $(this).closest('tr').remove();
    });

    parameters.on('change', '.type-select', function(){
        var $this = $(this);
        if ($this.val() == 2 || $this.val() == 3) {
            $this.closest('tr').find('.field-regexp').removeClass('hidden');
        } else {
            $this.closest('tr').find('.field-regexp').removeClass('hidden').addClass('hidden');
        }
    });
});
