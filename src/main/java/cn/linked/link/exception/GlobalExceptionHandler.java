package cn.linked.link.exception;

import cn.linked.link.constant.RedirectURI;
import cn.linked.link.entity.HttpResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoLoginException.class)
    @ResponseBody
    public HttpResult handleNoLoginException() {
        HttpResult result=new HttpResult();
        result.setCode(HttpResult.CODE_SESSION_INVALID);
        result.setRedirectURI(RedirectURI.URI_APP_LOGIN);
        return result;
    }

}
