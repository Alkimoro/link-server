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
        //todo 检查用户Session是否有效 无效则拦截 有效则重置过期时间
        NetworkData message=(NetworkData) msg;
        AppSession session=appSessionRepository.getSession(message.getSessionId());
        if(session==null||session.isExpired()) {
            if(session!=null) {
                long userId = session.getUserId();
                ctx.writeAndFlush(NetworkData.sessionInvalid(message.getSessionId()));
                userSocketManager.unBind(userId);
            }else {
                ctx.disconnect();
            }
        }else {
            session.setLastAccessedTime(System.currentTimeMillis());
            AttributeKey<Long> userIdKey=AttributeKey.valueOf(User.STRING_KEY_ID);
            if(message.getCode() == NetworkData.CODE_BIND_USER && !ctx.channel().hasAttr(userIdKey)) {
                long userId = session.getUserId();
                userSocketManager.bind(userId, ctx);
            }else {
                super.channelRead(ctx, msg);
            }
        }
    }

}
