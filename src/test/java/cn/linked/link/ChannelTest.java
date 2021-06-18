package cn.linked.link;

import cn.linked.link.entity.ChatMessage;
import cn.linked.link.entity.NetworkData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@Setter
@SpringBootTest(classes = ChannelTest.class)
public class ChannelTest extends ChannelInitializer<SocketChannel> {

    @Value("${chat-server.lengthFieldLength}")
    private int lengthFieldLength;
    @Value("${chat-server.maxContentLength}")
    private int maxContentLength;

    @Value("${chat-server.host}")
    private String host;
    @Value("${chat-server.port}")
    private int port;

    @Test
    public void startUser1() {
        String sessionId = "6ed5ff66-1d53-4f65-a6cf-10fb4103e206";
        Channel channel = prepare(); assert channel != null;
        NetworkData data = new NetworkData();
        data.setCode(NetworkData.CODE_BIND_USER);
        data.setSessionId(sessionId);
        System.out.println(data.toJsonString());
        channel.writeAndFlush(data.toJsonString());
        new Thread(()->{
            try {
                Thread.sleep(2000);
                NetworkData chatMsg = new NetworkData();
                chatMsg.setCode(NetworkData.CODE_CHAT_MSG);
                chatMsg.setSessionId(sessionId);
                ChatMessage message = new ChatMessage();
                message.setGroupId("60c56ce02e494a68c27645cb");
                message.setOwner(100001L);
                message.setMessage("你好");
                chatMsg.setData(message);
                System.out.println(chatMsg.toJsonString());
                channel.writeAndFlush(chatMsg.toJsonString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        try { channel.closeFuture().sync(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Test
    public void startUser2() {
        String sessionId = "a354b80d-1e02-4301-995f-3b3692ec86c3";
        Channel channel = prepare(); assert channel != null;
        NetworkData data = new NetworkData();
        data.setCode(NetworkData.CODE_BIND_USER);
        data.setSessionId(sessionId);
        System.out.println(data.toJsonString());
        channel.writeAndFlush(data.toJsonString());
        try { channel.closeFuture().sync(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast("idleStateHandler",new IdleStateHandler(0,5,0));

        socketChannel.pipeline().addLast("packageDecoder",
                new LengthFieldBasedFrameDecoder(lengthFieldLength+maxContentLength,
                        0,lengthFieldLength,0,lengthFieldLength,true));
        socketChannel.pipeline().addLast("packageEncoder", new LengthFieldPrepender(lengthFieldLength,false));

        socketChannel.pipeline().addLast("utf8Decoder", new StringDecoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast("utf8Encoder", new StringEncoder(CharsetUtil.UTF_8));

        socketChannel.pipeline().addLast("heartbeatHandler",new ChannelInboundHandlerAdapter() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                if (evt instanceof IdleStateEvent) {
                    IdleState state = ((IdleStateEvent) evt).state();
                    if (state == IdleState.WRITER_IDLE) {
                        NetworkData data = new NetworkData();
                        data.setCode(NetworkData.CODE_HEARTBEAT);
                        data.setSessionId("177756c8-6a71-4e2b-b2f0-5207dff6567b");
                        System.out.println("发送心跳:"+data.toJsonString());
                        ctx.channel().writeAndFlush(data.toJsonString());
                    }
                } else {
                    super.userEventTriggered(ctx, evt);
                }
            }
        });

        socketChannel.pipeline().addLast("testHandler",new TestHandler());
    }

    public static class TestHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("收到消息:"+msg);
        }
    }

    private Channel prepare() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(this);
        try {
            // 线程同步等待到通道链接成功
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            return channelFuture.channel();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
