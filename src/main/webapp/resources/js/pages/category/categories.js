$(function(){
    $('.category-delete').on('click', function(){
        if (confirm('Удалить категорию?')) {
            var elem = $(this);
            var categoryId = elem.data('id');
            $.ajax({
                type: "POST",
                url: categoryDeleteUrl,
                data: {categoryId: categoryId},
                success: function(data){
                    if (data == true) {
                        elem.closest('tr').remove();
                    }
                }
            });
        }
    });
});