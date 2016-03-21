$(function(){
    var treesHolder = $('#treesHolder');
    var filesHolder = $('#filesHolder');
    treesHolder.jstree({
        "core" : {
            "data": trees,
            "check_callback": true,
            "multiple" : false,
            "animation" : 0,
            "dblclick_toggle": false
        }
    });
    treesHolder.on("select_node.jstree", function (e, data) {
        var categoryId = parseId(data.selected[0]);
        location.search = "?categoryId=" + categoryId;
    });
});