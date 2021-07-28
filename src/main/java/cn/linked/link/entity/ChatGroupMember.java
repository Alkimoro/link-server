package cn.linked.link.entity;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
public class ChatGroupMember {

    public static final int AUTHORITY_OWNER = 1;
    public static final int AUTHORITY_ADMIN = 2;
    public static final int AUTHORITY_NORMAL = 3;

    @Indexed(unique = true)
    private Long userId;// 用户ID

    // 用户身份权限
    private Integer authority;

    @Transient
    private String groupId;

    private String alias;
    // 用户分组 组名
    private String userGroupName;
    // 用户已读消息的最大 SequenceNum
    private Long haveReadMessageMaxSequenceNum;
    private Date joinTime;

}
