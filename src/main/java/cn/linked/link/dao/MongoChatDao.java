package cn.linked.link.dao;

import cn.linked.link.entity.ChatGroup;
import cn.linked.link.entity.ChatGroupMember;
import cn.linked.link.entity.ChatGroupSequenceInc;
import cn.linked.link.entity.ChatMessage;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Setter
@Component
public class MongoChatDao {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private MongoTemplate mongoTemplate;

    public ChatGroup findChatGroupById(String groupId) {
        Query query=new Query(Criteria.where("id").is(groupId));
        return mongoTemplate.findOne(query,ChatGroup.class,"chat_group");
    }

    public List<ChatMessage> findChatMessage(String groupId, Long maxSequenceNumber, Integer num) {
        List<ChatMessage> result = new ArrayList<>();
        Query query = null;
        if(groupId == null || num == null || num == 0) {
            return result;
        }else if(maxSequenceNumber == null || maxSequenceNumber == Long.MAX_VALUE || maxSequenceNumber < 0) {
            // 查询 SequenceNumber 最大的 num 条记录
            query = new Query(Criteria.where("groupId").is(groupId));
        }else { // 查询 SequenceNumber <= maxSequenceNumber 的 num 条记录
            query = new Query(Criteria.where("groupId").is(groupId).and("sequenceNumber").lte(maxSequenceNumber));
        }
        // 降序排列
        query.with(Sort.by(Sort.Direction.DESC,"sequenceNumber"));
        query.limit(num);
        result = mongoTemplate.find(query,ChatMessage.class,"chat_message");
        return result;
    }

    public ChatGroup addChatGroupMember(String groupId, ChatGroupMember member) {
        if(groupId != null && member != null && member.getId() != null) {
            Query query = new Query(Criteria.where("id").is(groupId).and("memberList.id").ne(member.getId()));
            Update update = new Update();
            update.push("memberList",member);
            FindAndModifyOptions options = new FindAndModifyOptions();
            // true 先查询 如果没有符合条件的 会执行插入 插入的值是查询值 ＋ 更新值
            options.upsert(false);
            // 返回当前最新值
            options.returnNew(true);
            return mongoTemplate.findAndModify(query,update,options,ChatGroup.class,"chat_group");
        }
        return null;
    }

    public ChatGroup addChatGroup(int type) {
        ChatGroup chatGroup = new ChatGroup();
        if(type == ChatGroup.TYPE_PRIVATE) {
            chatGroup.setType(ChatGroup.TYPE_PRIVATE);
        }else {
            chatGroup.setType(ChatGroup.TYPE_GROUP);
        }
        return mongoTemplate.insert(chatGroup,"chat_group");
    }

    public void saveMessage(ChatMessage chatMessage) {
        if(chatMessage!=null) {
            chatMessage.setId(null);
            Long sequenceNumber= ChatGroupSequenceInc.increase(chatMessage.getGroupId(), mongoTemplate);
            chatMessage.setSequenceNumber(sequenceNumber);
            // 存储失败将会造成 该 chatGroup 的 sequenceNum 不连续
            try {
                mongoTemplate.save(chatMessage, "chat_message");
            }catch (Exception e) {
                log.warn("saveMessage failed message:{}", chatMessage);
            }
        }
    }

}
