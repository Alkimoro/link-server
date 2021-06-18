package cn.linked.link.entity;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
@Document(collection = "chat_message")
@CompoundIndex(def = "{'groupId': 1, 'sequenceNumber': 1}")
public class ChatMessage {

    @MongoId(targetType = FieldType.OBJECT_ID)
    private String id;

    @Transient
    private Long ackId;

    private Long owner;

    @Field(targetType = FieldType.OBJECT_ID)
    private String groupId;

    private Long sequenceNumber;
    private String message;
    private Date sendTime;

}
