package cn.linked.link;

import cn.linked.link.dao.MongoChatDao;
import cn.linked.link.entity.ChatGroup;
import cn.linked.link.entity.ChatGroupMember;
import cn.linked.link.entity.ChatGroupSequenceInc;
import cn.linked.link.entity.ChatMessage;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
public class MongoTest {

    @Setter
    @Resource
    MongoChatDao mongoChatDao;

    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void test() {
//        ChatGroup chatGroup = mongoChatDao.addChatGroup(ChatGroup.TYPE_PRIVATE);
//        System.out.println(chatGroup);
//        60c56ce02e494a68c27645cb
//        ChatGroupMember member = new ChatGroupMember();
//        member.setId(100001L);
//        member.setAlias("link__");
//        member.setJoinTime(new Date());
//        ChatGroup chatGroup = mongoChatDao.addChatGroupMember("60c56ce02e494a68c27645cb",member);
//        System.out.println(chatGroup);
        ChatMessage message = new ChatMessage();
        message.setOwner(100001L);
        message.setMessage("hhhhhh");
        message.setGroupId("60c56ce02e494a68c27645cb");
        message.setSendTime(new Date());
        message.setId("");
        mongoChatDao.saveMessage(message);
    }

}
