package cn.linked.link.entity;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetworkData {

    /**
     *  700 心跳
     *  701 绑定用户
     *  702 聊天消息
     *  703 聊天消息确认
     *  704 用户Session过期
     * */
    public static final int CODE_HEARTBEAT = 700;
    public static final int CODE_BIND_USER = 701;
    public static final int CODE_CHAT_MSG = 702;
    public static final int CODE_CHAT_ACK = 703;
    public static final int CODE_SESSION_INVALID = 704;

    private int code;
    private String msg;
    private Object data;
    private String sessionId;

    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    public static NetworkData sessionInvalid(String sessionId) {
        NetworkData networkData=new NetworkData();
        networkData.setCode(CODE_SESSION_INVALID);
        networkData.setSessionId(sessionId);
        return networkData;
    }

    public static NetworkData toChatAckMessage(ChatMessage message) {
        NetworkData data=new NetworkData();
        data.code=CODE_CHAT_ACK;
        ChatMessage ackMessage=new ChatMessage();
        ackMessage.setAckId(message.getAckId());
        ackMessage.setSequenceNumber(message.getSequenceNumber());
        data.data=ackMessage;
        return data;
    }

    public static NetworkData formChatMessage(ChatMessage message) {
        NetworkData data=new NetworkData();
        data.code=CODE_CHAT_MSG;
        data.data=message;
        return data;
    }

}