package cn.linked.link.component;

import cn.linked.link.exception.NoLoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginHandlerInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session=request.getSession(false);
        log.info("RequestUIR:{}",request.getRequestURI());
        if((request.getRequestURI().equals("/link/userController/isSessionInvalid"))
                ||(request.getRequestURI().equals("/link/userController/login"))
                ||(session!=null&&session.getAttribute("userId")!=null)) {
            return true;
        }
        throw new NoLoginException();
    }

}
