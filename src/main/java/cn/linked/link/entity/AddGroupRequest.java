package cn.linked.link.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
@Document(collection = "add_group_request")
@CompoundIndex(def = "{'groupId': 1, 'requestUserId': 1}")
public class AddGroupRequest {

    /**
     *  requestUserId和groupId 一定的情况下 同一时间只能有一个PENDING状态的Request记录
     * */
    public static final int STATE_PENDING = 0;
    public static final int STATE_ACCEPTED = 1;
    public static final int STATE_REJECTED = 2;

    @MongoId(targetType = FieldType.OBJECT_ID)
    private String id;

    private Long requestUserId;
    private String groupId;
    private String verifyInfo;
    // 用户给该群聊设置的分组 组名
    private String userGroupName;

    private Integer state;

    private Date createTime;

}
