$(function(){
    var treesHolder = $('#treesHolder');
    var addNewTree = $('#addNewTree');
    var addCategoryModal = $('#addCategoryModal');
    var newCategoryParent = undefined;

    $('#submitNewCategory').on('click', function(){
        var form = $(this).closest('form');
        var titleElem = form.find('#categoryTitle');
        var title = titleElem.val().trim();
        var error = false;
        form.find('.has-error').removeClass('has-error');
        if (title.length == 0) {
            error = true;
            titleElem.addClass('has-error');
        }
        if (newCategoryParent == undefined) {
            error = true;
        }
        if (!error) {
            var newTreeElem = treesHolder.jstree(
                "create_node",
                newCategoryParent.id,
                {text: title},
                'last',
                false,
                false
            );
            categoryAjax(newCategoryParent.id, title, newTreeElem);
        }
    });

    treesHolder.jstree({
        "core" : {
            "data": trees,
            "check_callback": true,
            "multiple" : false,
            "animation" : 0
        },
        "plugins" : [ "dnd", "contextmenu" ],
        'contextmenu': {
            'items': function ($node) {
                return {
                    "Add": {
                        "label": "Добавить категорию",
                        "action": function (obj) {
                            // добавление категории в дерево
                            newCategoryParent = $node;
                            addCategoryModal.arcticmodal();
                        }
                    },
                    "Delete": {
                        "label": "Удалить",
                        "action": function (obj) {
                            // удаление категории из дерева
                            if (confirm('Удалить элемент меню?')) {
                                $.ajax({
                                    url: deleteTreesUrl,
                                    method: "post",
                                    dataType: "json",
                                    data: {treeId: $node.id},
                                    beforeSend: function(){
                                    },
                                    success: function(data){
                                        var children = $node.children;
                                        $(children).each(function (i, elemId) {
                                            treesHolder.jstree(
                                                "move_node",
                                                elemId,
                                                $node.parent
                                            );
                                        });
                                        treesHolder.jstree(
                                            "delete_node",
                                            $node.id
                                        );
                                    },
                                    error: function(){
                                        alert('Ошибка при сохранении');
                                    },
                                    complete: function() {
                                    }
                                });
                            }
                        }
                    }
                };
            }
        }
    });

    addNewTree.on('click', function(){
        var title = prompt('Название', '');
        if (title != null && title != '') {
            var treeId = treesHolder.jstree(
                "create_node",
                null,
                {text: title},
                'last',
                false,
                false
            );
            categoryAjax("#", title, treeId);
        }
    });

    function categoryAjax(parent, title, treeId) {
        $.ajax({
            url: addCategoryToTreeUrl,
            method: "post",
            dataType: "json",
            data: {parent: parent, title: title, treeId: treeId},
            beforeSend: function(){
            },
            success: function(data){
                toastr.success('Without any options', 'Категория сохранена')
            },
            error: function(){
                treesHolder.jstree(
                    "delete_node",
                    treeId
                );
                alert('Ошибка при сохранении');
            },
            complete: function(){
                addCategoryModal.arcticmodal('close');
            }
        });
    }
});