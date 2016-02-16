$(function () {
    $chpu = $('#chpu');
    $title = $('#title');
    $title.on('keyup', function(){
        var str = $(this).val();
        var chpu = translite(str.toLocaleLowerCase());
        $chpu.val(chpu);
    });
});

function translite(str){
    var arr={'а':'a', 'б':'b', 'в':'v', 'г':'g', 'д':'d', 'е':'e', 'ж':'g', 'з':'z', 'и':'i', 'й':'y', 'к':'k',
        'л':'l', 'м':'m', 'н':'n', 'о':'o', 'п':'p', 'р':'r', 'с':'s', 'т':'t', 'у':'u', 'ф':'f', 'ы':'i', 'э':'e',
        'А':'a', 'Б':'B', 'В':'v', 'Г':'g', 'Д':'d', 'Е':'e', 'Ж':'g', 'З':'z', 'И':'i', 'Й':'y', 'К':'k', 'Л':'l',
        'М':'m', 'Н':'n', 'О':'o', 'П':'p', 'Р':'r', 'С':'s', 'Т':'t', 'У':'u', 'Ф':'f', 'Ы':'i', 'Э':'e', 'ё':'yo',
        'х':'h', 'ц':'ts', 'ч':'ch', 'ш':'sh', 'щ':'shch', 'ъ':'', 'ь':'', 'ю':'yu', 'я':'ya', 'Ё':'yo', 'Х':'h',
        'Ц':'ts', 'Ч':'ch', 'Ш':'sh', 'Щ':'shch', 'Ъ':'', 'Ь':'', 'Ю':'yu', 'Я':'ya', ' ': '-'};
    var replacer=function(a){return arr[a]||a};
    return str.replace(/[А-яёЁ]/g,replacer)
}