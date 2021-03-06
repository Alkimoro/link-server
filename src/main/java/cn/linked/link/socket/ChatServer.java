package cn.linked.link.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Setter
@Component
@ConfigurationProperties(prefix = "chat-server")
public class ChatServer extends ChannelInitializer<SocketChannel> {

    private String host;
    private int port;
    private int backlog;

    private int workThreadNum;

    private int lengthFieldLength;
    private int maxContentLength;

    private int heartbeatIdle;

    @Resource
    public ChatHandler chatHandler;
    @Resource
    public InboundDataParseHandler inboundDataParseHandler;
    @Resource
    public LoginInterceptorHandler loginInterceptorHandler;
    @Resource
    public HeartbeatListenHandler heartbeatListenHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast("idleStateHandler",new IdleStateHandler(heartbeatIdle,0,0));

        socketChannel.pipeline().addLast("packageDecoder",
                new LengthFieldBasedFrameDecoder(lengthFieldLength+maxContentLength,
                        0,lengthFieldLength,0,lengthFieldLength,true));
        socketChannel.pipeline().addLast("packageEncoder", new LengthFieldPrepender(lengthFieldLength,false));

        socketChannel.pipeline().addLast("utf8Decoder", new StringDecoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast("utf8Encoder", new StringEncoder(CharsetUtil.UTF_8));

        socketChannel.pipeline().addLast("inboundDataParseHandler",inboundDataParseHandler);

        socketChannel.pipeline().addLast("heartbeatHandler",heartbeatListenHandler);
        // ===??????Handler===
        socketChannel.pipeline().addLast("loginInterceptorHandler",loginInterceptorHandler);
        socketChannel.pipeline().addLast("chatHandler",chatHandler);
    }

    public void start() {
        InetSocketAddress socketAddress = new InetSocketAddress(host, port);
        // new ??????????????????
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // new ?????????????????????
        EventLoopGroup workGroup = new NioEventLoopGroup(workThreadNum);
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(this)
                .localAddress(socketAddress)
                // ??????????????????
                .option(ChannelOption.SO_BACKLOG, backlog)
                // ????????????????????????????????????,TCP?????????????????????????????????????????????
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            // ????????????,???????????????????????????
            ChannelFuture future = bootstrap.bind(socketAddress).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // ??????????????????
            bossGroup.shutdownGracefully();
            // ?????????????????????
            workGroup.shutdownGracefully();
        }
    }

}
