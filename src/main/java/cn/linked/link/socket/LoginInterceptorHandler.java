package cn.linked.link.socket;

import cn.linked.link.component.AppSession;
import cn.linked.link.component.AppSessionRepository;
import cn.linked.link.entity.NetworkData;
import cn.linked.link.entity.User;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@ChannelHandler.Sharable
@Component
public class LoginInterceptorHandler extends ChannelInboundHandlerAdapter {

    @Resource
    @Setter
    private AppSessionRepository appSessionRepository;
    @Resource
    @Setter
    private UserChannelManager userSocketManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetworkData<?> message = (NetworkData<?>) msg;
        AppSession session = appSessionRepository.getSession(message.getSessionId());
        if(session == null || session.isExpired()) {
            if(session != null) {
                long userId = session.getUserId();
                ctx.writeAndFlush(NetworkData.sessionInvalid(message.getSessionId()));
                userSocketManager.unBind(userId);
            }
            ctx.disconnect();
        }else {
            session.setLastAccessedTime(System.currentTimeMillis());
            AttributeKey<Long> userIdKey = AttributeKey.valueOf(User.STRING_KEY_ID);
            // channel 不能重复绑定
            if(message.getCode() == NetworkData.CODE_BIND_USER && !ctx.channel().hasAttr(userIdKey)) {
                long userId = session.getUserId();
                NetworkData<Boolean> bindAck = new NetworkData<>();
                bindAck.setCode(NetworkData.CODE_BIND_ACK);
                bindAck.setData(true);
                ctx.channel().writeAndFlush(bindAck.toJsonString());
                // 保证bindAck是Client第一个收到的消息
                userSocketManager.bind(userId, ctx);
            }else {
                super.channelRead(ctx, msg);
            }
        }
    }

}
