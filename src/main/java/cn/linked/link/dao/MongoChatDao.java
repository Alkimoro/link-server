package cn.linked.link.dao;

import cn.linked.link.component.AutoIncTool;
import cn.linked.link.entity.ChatGroup;
import cn.linked.link.entity.ChatGroupMember;
import cn.linked.link.entity.ChatGroupType;
import cn.linked.link.entity.ChatMessage;
import lombok.NonNull;
import lombok.Setter;
import org.bson.types.ObjectId;
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
import java.util.Date;
import java.util.List;

@Setter
@Component
public class MongoChatDao {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private AutoIncTool autoIncTool;

    public ChatGroup findChatGroupWithMemberById(String groupId) {
        Query query=new Query(Criteria.where("id").is(groupId));
        return mongoTemplate.findOne(query,ChatGroup.class,"chat_group");
    }

    /**
     *  num < 0 默认拉取 1 ~ maxSequenceNumber的所有数据
     * */
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
        if(num >= 0) {
            query.limit(num);
        }
        result = mongoTemplate.find(query,ChatMessage.class,"chat_message");
        return result;
    }

    public ChatGroup addChatGroupMember(String groupId, ChatGroupMember member) {
        if(groupId != null && member != null && member.getUserId() != null) {
            if(member.getAuthority() == null) { member.setAuthority(ChatGroupMember.AUTHORITY_NORMAL); }
            Query query = new Query(Criteria.where("id").is(groupId).and("memberList.userId").ne(member.getUserId()));
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

    public ChatGroup addChatGroup(ChatGroupType type) {
        ChatGroup chatGroup = new ChatGroup();
        chatGroup.setCreateTime(new Date());
        chatGroup.setLevel(1);
        chatGroup.setName(ChatGroup.DEFAULT_NAME);
        chatGroup.setMemberList(new ArrayList<>());
        if(type.getIdType() == ChatGroup.ID_TYPE_OBJECT_ID) {
            // 该类型的ChatGroup ID为ObjectID 对于用户是透明的
            chatGroup.setId(new ObjectId().toString());
        }else {
            // 该类型的ChatGroup ID为自增ID 方便记忆和搜索
            chatGroup.setId(autoIncTool.increase(AutoIncTool.KEY_CHAT_GROUP) + "");
        }
        chatGroup.setType(type);
        return mongoTemplate.insert(chatGroup,"chat_group");
    }

    public void saveMessage(ChatMessage chatMessage) {
        if(chatMessage!=null) {
            chatMessage.setId(null);
            Long sequenceNumber = autoIncTool.increase(chatMessage.getGroupId());
            chatMessage.setSequenceNumber(sequenceNumber);
            // 存储失败将会造成 该 chatGroup 的 sequenceNum 不连续
            try {
                mongoTemplate.save(chatMessage, "chat_message");
            }catch (Exception e) {
                log.warn("saveMessage failed message:{}", chatMessage);
            }
        }
    }

    public ChatGroup findChatGroupById(@NonNull String groupId) {
        Query query = new Query(Criteria.where("id").is(groupId));
        query.fields().exclude("memberList");
        return mongoTemplate.findOne(query, ChatGroup.class);
    }

    public List<ChatGroup> findChatGroupByUserId(@NonNull Long userId) {
        Query query = new Query(Criteria.where("memberList.userId").is(userId));
        query.fields().exclude("memberList");
        return mongoTemplate.find(query, ChatGroup.class);
    }

    public List<ChatGroupMember> findChatGroupMember(@NonNull String groupId) {
        Query query = new Query(Criteria.where("id").is(groupId));
        query.fields().include("memberList");
        ChatGroup chatGroup = mongoTemplate.findOne(query, ChatGroup.class);
        if(chatGroup != null) {
            List<ChatGroupMember> memberList = chatGroup.getMemberList();
            for(ChatGroupMember member : memberList) {
                member.setGroupId(groupId);
            }
            return memberList;
        }
        return null;
    }

    public ChatGroupMember findUserChatGroupMember(@NonNull String groupId, @NonNull Long userId) {
        Query query = new Query(Criteria.where("id").is(groupId).and("memberList.userId").is(userId));
        query.fields().elemMatch("memberList", Criteria.where("userId").is(userId));
        ChatGroup chatGroup = mongoTemplate.findOne(query, ChatGroup.class);
        if(chatGroup != null && chatGroup.getMemberList() != null && chatGroup.getMemberList().size() == 1) {
            ChatGroupMember member = chatGroup.getMemberList().get(0);
            member.setGroupId(groupId);
            return member;
        }
        return null;
    }

    public boolean setUserHaveReadMessageMaxSequenceNum(Long userId, String groupId, Long maxSequenceNum) {
        Query query = new Query(Criteria.where("id").is(groupId).and("memberList.userId").is(userId));
        Update update = new Update();
        update.set("memberList.$.haveReadMessageMaxSequenceNum", maxSequenceNum);
        return mongoTemplate.updateFirst(query, update, ChatGroup.class).getMatchedCount() == 1;
    }

}
