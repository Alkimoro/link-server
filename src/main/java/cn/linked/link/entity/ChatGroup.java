package cn.linked.link.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "chat_group")
public class ChatGroup {

    public static final int TYPE_PRIVATE = 1;
    public static final int TYPE_GROUP = 2;

    // todo 建立和优化索引 主要是通过用户id查找groupId
    @MongoId(targetType = FieldType.OBJECT_ID)
    private String id;

    @Indexed
    private String name = "default";
    private String description;
    private Integer type = TYPE_GROUP;
    private String imageUri;
    private Integer level = 1;
    private Date createTime = new Date();
    private List<ChatGroupMember> memberList = new ArrayList<>();

}
