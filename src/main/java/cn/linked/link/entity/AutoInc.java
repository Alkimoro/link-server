package cn.linked.link.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "auto_inc")
public class AutoInc {

    @MongoId(targetType = FieldType.OBJECT_ID)
    private String id;

    @Indexed(unique = true)
    @Field(targetType = FieldType.STRING)
    private String key;

    private Long curSequenceNumber = 0L;

}
