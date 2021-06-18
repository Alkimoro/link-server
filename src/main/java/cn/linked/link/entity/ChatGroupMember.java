package cn.linked.link.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
public class ChatGroupMember {

    @Indexed(unique = true)
    private Long id;// 用户ID

    private String alias;
    private Date joinTime;

}
