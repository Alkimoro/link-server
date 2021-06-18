package cn.linked.link.service;

import cn.linked.link.dao.UserDao;
import cn.linked.link.entity.HttpResult;
import cn.linked.link.entity.User;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class UserService {

    @Resource
    @Setter
    private UserDao userDao;

    @Transactional
    public User login(Long id, String password) {
        User user = userDao.getUserById(id);
        if(password != null && user != null && password.equals(user.getPassword())) {
            return user;
        }else {
            return null;
        }
    }

}
