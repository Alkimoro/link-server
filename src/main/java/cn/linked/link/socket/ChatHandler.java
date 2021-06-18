package cn.linked.link.socket;

import cn.linked.link.entity.ChatMessage;
import cn.linked.link.entity.NetworkData;
import cn.linked.link.entity.User;
import cn.linked.link.service.ChatService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Setter
@Component
@ChannelHandler.Sharable
public class ChatHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private UserChannelManager manager;

    @Resource
    private ChatService chatService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel active");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetworkData message = (NetworkData) msg;
        log.info("服务器收到消息:{}", message.toJsonString());
        if(message.getCode() == NetworkData.CODE_CHAT_MSG) {
            ChatMessage chatMessage = JSON.toJavaObject((JSONObject) message.getData(), ChatMessage.class);
            chatService.userChat(chatMessage, ctx);
        }else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        AttributeKey<Long> userIdKey=AttributeKey.valueOf(User.STRING_KEY_ID);
        Long userId=ctx.channel().attr(userIdKey).get();
        manager.unBind(userId);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.disconnect();
        super.exceptionCaught(ctx,cause);
    }

}
