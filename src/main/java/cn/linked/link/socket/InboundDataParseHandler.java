package cn.linked.link.socket;

import cn.linked.link.entity.NetworkData;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class InboundDataParseHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetworkData message= JSON.parseObject(msg.toString(),NetworkData.class);
        super.channelRead(ctx, message);
    }
}
