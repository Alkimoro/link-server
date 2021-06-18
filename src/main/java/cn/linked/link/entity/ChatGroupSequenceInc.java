package cn.linked.link.entity;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

@Data
@Document(collection = "chat_group_sequence")
public class ChatGroupSequenceInc {

    @MongoId(targetType = FieldType.OBJECT_ID)
    private String id;

    @Indexed(unique = true)
    @Field(targetType = FieldType.OBJECT_ID)
    private String groupId;

    private Long curSequenceNumber = 0L;

    /**
     *  使curSequenceNumber字段原子增加
     *  @return 增加后的值
     * */
    public static Long increase(String groupId,@NonNull MongoTemplate mongoTemplate) {
        Query query=new Query(Criteria.where("groupId").is(groupId));
        Update update=new Update();
        update.inc("curSequenceNumber", 1);
        FindAndModifyOptions options=new FindAndModifyOptions();
        // true 先查询 如果没有符合条件的 会执行插入 插入的值是查询值 ＋ 更新值
        options.upsert(true);
        // 返回当前最新值
        options.returnNew(true);
        return Objects.requireNonNull(mongoTemplate.findAndModify(query, update, options,
                ChatGroupSequenceInc.class, "chat_group_sequence")).curSequenceNumber;
    }

}
