$(function(){
    var treesHolder = $('#treesHolder');
    treesHolder.jstree({
        "core" : {
            "data": trees,
            "check_callback": true,
            "multiple" : false,
            "animation" : 0
        }
    });
});