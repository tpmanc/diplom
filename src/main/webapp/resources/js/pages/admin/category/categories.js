var treesHolder;
$(function(){
    treesHolder = $('#treesHolder');
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
            var parentId = parseId(newCategoryParent.id);
            categoryAjax(parentId, title, newTreeElem);
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
                        "label": "Добавить подкатегорию",
                        "action": function (obj) {
                            // добавление категории в дерево
                            newCategoryParent = $node;
                            addCategoryModal.arcticmodal();
                        }
                    },
                    "Update": {
                        "label": "Переименовать",
                        "action": function (obj) {
                            // переименовать категорию
                            var prevTitle = treesHolder.jstree("get_text", $node.id);
                            var title = prompt('Новое название', prevTitle);
                            if (title != null && title != '') {
                                var id = parseId($node.id);
                                $.ajax({
                                    url: renameCategoryUrl,
                                    method: "post",
                                    dataType: "json",
                                    data: {id: id, title: title},
                                    beforeSend: function(){
                                    },
                                    success: function(data){
                                        treesHolder.jstree(
                                            "rename_node",
                                            $node.id,
                                            data.title
                                        );
                                    },
                                    error: function(){
                                        alert('Ошибка при сохранении');
                                    }
                                });
                            }
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

    treesHolder.on("move_node.jstree", function (e, data) {
        var parentId = parseId(data.parent);
        var treeId = parseId(data.node.id);
        $.ajax({
            url: updatePosition,
            method: "post",
            data: {"treeId": treeId, "newParentId": parentId, "position": data.position},
            success: function(data){

            },
            error: function(){
                alert('Ошибка при сохранении');
            }
        });
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
            categoryAjax(0, title, treeId);
        }
    });

    function categoryAjax(parent, title, treeId) {
        $.ajax({
            url: addCategoryToTreeUrl,
            method: "post",
            dataType: "json",
            data: {parent: parent, title: title, position: getNodePosition(treeId)},
            beforeSend: function(){
            },
            success: function(data){
                toastr.success('Without any options', 'Категория сохранена');
                treesHolder.jstree(
                    "set_id",
                    treeId,
                    "jst_"+data.id
                );
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

    function getNodePosition(nodeId) {
        var tree = treesHolder.jstree(true);
        var node = tree.get_node(nodeId);
        var parent = tree.get_node(node.parent);
        return $.inArray(node.id, parent.children);
    }

    function parseId(str) {
        var id = 0;
        if (str == '#') {
            id = 0;
        } else {
            id = parseInt(str.replace('jst_', ''));
        }
        return id;
    }
});