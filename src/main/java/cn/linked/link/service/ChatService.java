package cn.linked.link.service;

import cn.linked.link.component.AppSessionRepository;
import cn.linked.link.dao.MongoChatDao;
import cn.linked.link.entity.*;
import cn.linked.link.socket.UserChannelManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Service
public class ChatService {

    @Resource
    private UserChannelManager manager;
    @Resource
    private MongoChatDao mongoChatDao;
    @Resource
    private AppSessionRepository appSessionRepository;

    public @NonNull List<ChatMessage> getChatMessage(String groupId, Long maxSequenceNumber, Integer num) {
        return mongoChatDao.findChatMessage(groupId, maxSequenceNumber, num);
    }

    public int userChat(ChatMessage message, ChannelHandlerContext ctx) {
        if(message!=null) {
            ChatGroup chatGroup=mongoChatDao.findChatGroupById(message.getGroupId());
            if(chatGroup!=null) {
                message.setSendTime(new Date());
                AttributeKey<Long> userIdKey=AttributeKey.valueOf(User.STRING_KEY_ID);
                message.setOwner(ctx.channel().attr(userIdKey).get());
                mongoChatDao.saveMessage(message);
                ctx.writeAndFlush(NetworkData.toChatAckMessage(message).toJsonString());
                for(int i=0;i<chatGroup.getMemberList().size();i++) {
                    Long memberId=chatGroup.getMemberList().get(i).getId();
                    if(!memberId.equals(message.getOwner())) {
                        UserChannel context = manager.getUserChannel(memberId);
                        if (context != null && context.getChannelToUse() != null) {
                            context.getChannelContext().writeAndFlush(NetworkData.formChatMessage(message).toJsonString());
                        }
                    }
                }
                return 0;
            }
        }
        return -1;
    }

}
