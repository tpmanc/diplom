$(function(){
    var treesHolder = $('#treesHolder');
    var addNewTree = $('#addNewTree');
    var addCategoryModal = $('#addCategoryModal');

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
                            // TODO: добавление категории
                            addCategoryModal.arcticmodal();
                            //var title = prompt('Название', '');
                            //if (title != '') {
                            //    var treeId = treesHolder.jstree(
                            //        "create_node",
                            //        $node.id,
                            //        {text: title},
                            //        'last',
                            //        false,
                            //        false
                            //    );
                            //    $.ajax({
                            //        url: saveTreesUrl,
                            //        method: "post",
                            //        dataType: "json",
                            //        data: {parent: $node.id, title: title, treeId: treeId},
                            //        beforeSend: function(){
                            //            NProgress.start();
                            //        },
                            //        success: function(data){
                            //            NProgress.done();
                            //
                            //        },
                            //        error: function(){
                            //            treesHolder.jstree(
                            //                "delete_node",
                            //                treeId
                            //            );
                            //            alert('Ошибка при сохранении');
                            //        },
                            //        complete: function(){
                            //            NProgress.done();
                            //        }
                            //    });
                            //}
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
                                        NProgress.start();
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
                                        NProgress.done();

                                    },
                                    error: function(){
                                        alert('Ошибка при сохранении');
                                    },
                                    complete: function(){
                                        NProgress.done();
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
        if (title != '') {
            var treeId = treesHolder.jstree(
                "create_node",
                null,
                {text: title},
                'last',
                false,
                false
            );
            $.ajax({
                url: saveTreesUrl,
                method: "post",
                dataType: "json",
                data: {parent: "#", title: title, treeId: treeId},
                beforeSend: function(){
                    NProgress.start();
                },
                success: function(data){
                    NProgress.done();

                },
                error: function(){
                    treesHolder.jstree(
                        "delete_node",
                        treeId
                    );
                    alert('Ошибка при сохранении');
                },
                complete: function(){
                    NProgress.done();
                }
            });
        }
    });
});