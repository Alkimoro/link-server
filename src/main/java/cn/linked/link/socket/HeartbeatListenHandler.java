package cn.linked.link.socket;

import cn.linked.link.entity.NetworkData;
import com.alibaba.fastjson.JSONException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class HeartbeatListenHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            NetworkData<?> message = (NetworkData<?>) msg;
            if(message.getCode() == NetworkData.CODE_HEARTBEAT) {
                log.info("服务器收到心跳:{}", message.toJsonString());
            }else {
                super.channelRead(ctx, msg);
            }
        }catch (JSONException e) {
            log.warn("JSON parse error:{}",msg.toString());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                ctx.disconnect();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
