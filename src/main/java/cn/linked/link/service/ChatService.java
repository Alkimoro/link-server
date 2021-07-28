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
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    public boolean setUserHaveReadMessageMaxSequenceNum(Long userId, String groupId, Long maxSequenceNum) {
        if(userId != null && groupId != null && maxSequenceNum != null && maxSequenceNum >= 0) {
            return mongoChatDao.setUserHaveReadMessageMaxSequenceNum(userId, groupId, maxSequenceNum);
        }
        return false;
    }

    public @NonNull List<ChatGroup> getChatGroup(Long userId) {
        if(userId != null) {
            return mongoChatDao.findChatGroupByUserId(userId);
        }
        return new ArrayList<>();
    }

    public @Nullable ChatGroup getChatGroupById(String groupId) {
        if(groupId != null) {
            return mongoChatDao.findChatGroupById(groupId);
        }
        return null;
    }

    /**
     * 获取用户能收到的最新消息
     *      TYPE 为 GROUP 和 PRIVATE的消息
     *      每个Group一条最新消息 结果按 sendTime 小 -> 大 排序
     * */
    public List<ChatMessage> getUserNewestChatMessage(Long userId) {
        List<ChatMessage> result = new ArrayList<>();
        if(userId != null) {
            List<ChatGroup> chatGroupList = mongoChatDao.findChatGroupByUserId(userId);
            // 表示获取近7天内的新消息
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -7);
            Date minTime = calendar.getTime();
            for(ChatGroup chatGroup : chatGroupList) {
                if(chatGroup.getType() == ChatGroupType.GROUP || chatGroup.getType() == ChatGroupType.PRIVATE) {
                    List<ChatMessage> temp = mongoChatDao.findChatMessage(chatGroup.getId(), Long.MAX_VALUE, 1);
                    if (temp.size() == 1) {
                        ChatMessage message = temp.get(0);
                        // 最新的记录是minTime之后的就返回列表temp
                        if (message.getSendTime() != null && message.getSendTime().after(minTime)) {
                            result.add(message);
                        }
                    }
                }
            }
        }
        result.sort((a,b) -> {
            return (int) (a.getSendTime().getTime() - b.getSendTime().getTime());
        });
        return result;
    }

    public int userChat(ChatMessage message, ChannelHandlerContext ctx) {
        if(message!=null) {
            ChatGroup chatGroup=mongoChatDao.findChatGroupWithMemberById(message.getGroupId());
            if(chatGroup!=null) {
                message.setSendTime(new Date());
                AttributeKey<Long> userIdKey=AttributeKey.valueOf(User.STRING_KEY_ID);
                message.setOwner(ctx.channel().attr(userIdKey).get());
                mongoChatDao.saveMessage(message);
                ctx.writeAndFlush(NetworkData.toChatAckMessage(message).toJsonString());
                for(int i=0;i<chatGroup.getMemberList().size();i++) {
                    Long memberId=chatGroup.getMemberList().get(i).getUserId();
                    if(!memberId.equals(message.getOwner())) {
                        UserChannel context = manager.getUserChannel(memberId);
                        if (context != null && context.getChannelToUse() != null) {
                            context.getChannel().writeAndFlush(NetworkData.formChatMessage(message).toJsonString());
                        }
                    }
                }
                return 0;
            }
        }
        return -1;
    }

    public List<ChatGroupMember> getChatGroupMember(String groupId) {
        if(groupId != null) {
            return mongoChatDao.findChatGroupMember(groupId);
        }
        return null;
    }

    public ChatGroupMember getUserChatGroupMember(String groupId, Long userId) {
        if(groupId != null && userId != null) {
            return mongoChatDao.findUserChatGroupMember(groupId, userId);
        }
        return null;
    }

}
