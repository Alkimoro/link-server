package cn.linked.link.controller;

import cn.linked.link.component.AppSessionRepository;
import cn.linked.link.entity.HttpResult;
import cn.linked.link.entity.User;
import cn.linked.link.service.UserService;
import com.alibaba.fastjson.JSON;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/userController")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    @Setter
    private UserService userService;

    @Resource
    @Setter
    private AppSessionRepository appSessionRepository;

    @PostMapping("/login")
    public HttpResult login(Long userId, String password, HttpServletRequest request) {
        HttpResult result=new HttpResult();
        User user = userService.login(userId,password);
        if(user != null) {
            result.setCode(HttpResult.CODE_SUCCESS);
            result.setData(JSON.toJSONString(user));
            HttpSession session=request.getSession(true);
            log.info("userId:{},sessionId:{}",userId,session.getId());
            session.setAttribute(User.STRING_KEY_ID,userId);
        }else {
            result.setCode(HttpResult.CODE_FAIL);
            result.setMsg("账号或密码错误");
        }
        return result;
    }

    @PostMapping("/isSessionInvalid")
    public HttpResult isSessionInvalid(String sessionId) {
        HttpResult result=new HttpResult();
        if(sessionId==null||appSessionRepository.getSession(sessionId).isExpired()) {
            result.setCode(HttpResult.CODE_SUCCESS);
            result.setData(true);
        }else {
            result.setCode(HttpResult.CODE_SUCCESS);
            result.setData(false);
        }
        return result;
    }

}
