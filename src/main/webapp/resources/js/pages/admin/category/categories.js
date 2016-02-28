var treesHolder;
$(function(){
    treesHolder = $('#treesHolder');
    var addNewTree = $('#addNewTree');
    var addCategoryModal = $('#addCategoryModal');
    var categoryTitle = $('#categoryTitle');
    var submitNewCategory = $('#submitNewCategory');
    var renameCategoryModal = $('#renameCategoryModal');
    var submitNewCategoryTitle = $('#submitNewCategoryTitle');
    var newCategoryTitle = $('#newCategoryTitle');
    var renameCategoryId = $('#renameCategoryId');
    var newCategoryParent = undefined;

    addCategoryModal.on('shown.bs.modal', function () {
        categoryTitle.val('');
        categoryTitle.focus();
    });
    renameCategoryModal.on('shown.bs.modal', function () {
        categoryTitle.focus();
    });

    categoryTitle.on('keyup', function(event){
        if(event.keyCode == 13){
            submitNewCategory.click();
        }
    });
    newCategoryTitle.on('keyup', function(event){
        if(event.keyCode == 13){
            submitNewCategoryTitle.click();
        }
    });

    // переимановать категорию
    submitNewCategoryTitle.on('click', function(){
        var form = $(this).closest('.form');
        var title = newCategoryTitle.val().trim();
        var id = parseInt(renameCategoryId.val());
        var error = false;
        form.find('.has-error').removeClass('has-error');
        if (title.length == 0) {
            error = true;
            newCategoryTitle.closest('.form-group').addClass('has-error').find('.help-block').html('<div>Введите название</div>');
        }
        if (isNaN(id) || id <= 0) {
            error = true;
        }
        if (!error) {
            $.ajax({
                url: renameCategoryUrl,
                method: "post",
                dataType: "json",
                data: {id: id, title: title},
                beforeSend: function(){
                },
                success: function(data){
                    if (data.error == false) {
                        treesHolder.jstree(
                            "rename_node",
                            "jst_" + id,
                            data.title
                        );
                        toastr.success('Категория переименована');
                    } else {
                        toastr.error('Ошибка при сохранении', data.msg);
                    }
                },
                complete: function(){
                    renameCategoryModal.modal('hide');
                },
                error: function(data){
                    toastr.error('Ошибка при сохранении', data.msg);
                }
            });
        }
    });

    // добавить новую категорию
    submitNewCategory.on('click', function(){
        var form = $(this).closest('.form');
        var titleElem = categoryTitle;
        var title = titleElem.val().trim();
        var error = false;
        form.find('.has-error').removeClass('has-error');
        if (title.length == 0) {
            error = true;
            titleElem.closest('.form-group').addClass('has-error').find('.help-block').html('<div>Введите название</div>');
        }
        var parent = '#';
        if (newCategoryParent != undefined) {
            parent = newCategoryParent.id;
        }
        if (!error) {
            var newTreeElem = treesHolder.jstree(
                "create_node",
                parent,
                {text: title},
                'last',
                false,
                false
            );
            var parentId = 0;
            if (newCategoryParent != undefined) {
                parentId = parseId(newCategoryParent.id);
            }
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
                            addCategoryModal.modal('show');
                        }
                    },
                    "Update": {
                        "label": "Переименовать",
                        "action": function (obj) {
                            // переименовать категорию
                            newCategoryTitle.val($node.text);
                            renameCategoryId.val(parseId($node.id));
                            renameCategoryModal.modal('show');
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
                                    data: {id: parseId($node.id)},
                                    beforeSend: function(){
                                    },
                                    success: function(data){
                                        if (data.error == false) {
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
                                            toastr.success('Категория удалена');
                                        } else {
                                            toastr.error('Ошибка при удалении', data.msg);
                                        }
                                    },
                                    error: function(data){
                                        toastr.error('Ошибка при удалении', data.msg);
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
                if (data.error == false) {
                    toastr.success('Порядок сохранен');
                } else {
                    toastr.error('Ошибка при сохранении', data.msg);
                }
            },
            error: function(data){
                toastr.error('Ошибка при сохранении', data.msg);
            }
        });
    });

    addNewTree.on('click', function(){
        newCategoryParent = undefined;
        addCategoryModal.modal('show');
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
                if (data.error == false) {
                    treesHolder.jstree(
                        "set_id",
                        treeId,
                        "jst_" + data.id
                    );
                    toastr.success('Категория добавлена');
                } else {
                    treesHolder.jstree(
                        "delete_node",
                        treeId
                    );
                    toastr.error('Ошибка при сохранении', data.msg);
                }
            },
            error: function(data){
                treesHolder.jstree(
                    "delete_node",
                    treeId
                );
                toastr.error('Ошибка при сохранении', data.msg);
            },
            complete: function(){
                addCategoryModal.modal('hide');
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