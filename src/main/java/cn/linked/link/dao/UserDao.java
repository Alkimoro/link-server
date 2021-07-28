package cn.linked.link.dao;

import cn.linked.link.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface UserDao {

    @Select("SELECT password FROM USER WHERE id=${value} LIMIT 1")
    String getPasswordById(Long id);

    @Select("SELECT * FROM USER WHERE id=${value} LIMIT 1")
    User getUserById(Long id);

    @Insert("INSERT INTO `user` (`id`, `name`, `password`, `gender`, `birthday`, `mail`, `phoneNumber`, `address`, `signature`, `imageUrl`, `createTime`) " +
            "VALUES(#{id}, #{name}, #{password}, #{gender}, #{birthday}, #{mail}, #{phoneNumber}, #{address}, #{signature}, #{imageUrl}, #{createTime})")
    void addUser(User user);

}
