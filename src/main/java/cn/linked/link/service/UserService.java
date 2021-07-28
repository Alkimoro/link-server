package cn.linked.link.service;

import cn.linked.link.component.AutoIncTool;
import cn.linked.link.dao.UserDao;
import cn.linked.link.entity.User;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserService {

    @Resource
    @Setter
    private UserDao userDao;

    @Resource
    private AutoIncTool autoIncTool;

    @Transactional
    public User login(Long id, String password) {
        User user = userDao.getUserById(id);
        if(password != null && user != null && password.equals(user.getPassword())) {
            return user;
        }else {
            return null;
        }
    }

    @Transactional
    public void addUser(User user) {
        if(user != null) {
            user.setId(autoIncTool.increase(AutoIncTool.KEY_USER));
            user.setCreateTime(new Date());
            userDao.addUser(user);
        }
    }

    @Transactional
    public User getUserById(Long id) {
        if(id == null) { return null; }
        return userDao.getUserById(id);
    }

}
