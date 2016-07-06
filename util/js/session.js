/**
 * ajax请求处理session过期
 * 在后端添加一个响应头信息
 */
$.ajaxSetup({  
    complete: function(xhr,status) {  
        var sessionStatus = xhr.getResponseHeader('sessionstatus');  
        if(sessionStatus == 'timeout') {  
            $("#re-login-modal").modal("show");
        }  
    }  
});  