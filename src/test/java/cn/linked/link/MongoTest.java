package cn.linked.link;

import cn.linked.link.component.AutoIncTool;
import cn.linked.link.dao.MongoChatDao;
import cn.linked.link.entity.ChatGroup;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;

@SpringBootTest
public class MongoTest {

    @Setter
    @Resource
    MongoChatDao mongoChatDao;

    @Resource
    AutoIncTool autoIncTool;

    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void test() {
//        ChatGroup chatGroup = mongoChatDao.addChatGroup(ChatGroup.TYPE_GROUP);
//        System.out.println(chatGroup);
//        String groupId = "100001";
//        System.out.println(mongoChatDao.findChatGroupById(groupId));
//        ChatGroupMember member = new ChatGroupMember();
//        member.setUserId(100001L);
//        member.setAlias("link__");
//        member.setJoinTime(new Date());
//        ChatGroup chatGroup = mongoChatDao.addChatGroupMember(groupId, member);
//        System.out.println(chatGroup);
//        ChatMessage message = new ChatMessage();
//        message.setOwner(100001L);
//        message.setMessage("hhhhhh");
//        message.setGroupId("60c56ce02e494a68c27645cb");
//        message.setSendTime(new Date());
//        message.setId("");
//        mongoChatDao.saveMessage(message);
//        System.out.println(mongoChatDao.findUserChatGroupMember(groupId, 100001L));
//        mongoChatDao.setUserHaveReadMessageMaxSequenceNum(100002L, groupId, 2L);
    }

}
