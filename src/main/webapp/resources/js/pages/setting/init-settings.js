$(function(){
    var logFileEnable = $('#logFileEnable');
    logFileEnable.on('change', function(){
        var isLogFileEnabled = logFileEnable.prop("checked");
        if (isLogFileEnabled) {
            $('#logFileGroup').find('input').removeAttr('disabled');
        } else {
            $('#logFileGroup').find('input').attr('disabled', 'disabled');
        }
    });

    var logSyslogEnable = $('#logSyslogEnable');
    logSyslogEnable.on('change', function(){
        var isLogSyslogEnabled = logSyslogEnable.prop("checked");
        if (isLogSyslogEnabled) {
            $('#logSyslogGroup').find('input').removeAttr('disabled');
        } else {
            $('#logSyslogGroup').find('input').attr('disabled', 'disabled');
        }
    });
});