package cn.linked.link.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "chat_group")
public class ChatGroup {

    public static final String DEFAULT_NAME = "default";

    public static final int ID_TYPE_AUTO_INC = 1;
    public static final int ID_TYPE_OBJECT_ID = 2;

    // todo 建立和优化索引 主要是通过用户id查找groupId
    @MongoId(targetType = FieldType.STRING)
    private String id;

    @Indexed
    private String name;
    private String description;
    private ChatGroupType type;
    // 当 type == TYPE_PRIVATE 时 该字段为 null; 因为在私聊情况下，不同用户视角下 image是不同的
    private String imageUri;
    // 等级
    private Integer level;
    private Date createTime;
    private List<ChatGroupMember> memberList;

}
