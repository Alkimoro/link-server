package cn.linked.link.socket;

import cn.linked.link.entity.UserChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserChannelManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<Long, UserChannel> userChannelMap=new ConcurrentHashMap<>();

    public UserChannel getUserChannel(Long userId) {
        return userChannelMap.get(userId);
    }

    public void unBind(Long userId) {
        if(userId != null) {
            UserChannel userChannel = userChannelMap.get(userId);
            if(userChannel!=null) {
                userChannelMap.remove(userId);
                userChannel.close();
            }
        }
    }

    public void bind(Long userId, ChannelHandlerContext ctx) {
        UserChannel userChannel=userChannelMap.get(userId);
        if(userChannel!=null) {
            if(userChannel.getChannel()!=null) {
                userChannel.getChannel().close();
            }
        }else {
            userChannel=new UserChannel(userId);
            userChannelMap.put(userId,userChannel);
        }
        Channel channel=ctx.channel();
        AttributeKey<Long> userIdKey=AttributeKey.valueOf("userId");
        channel.attr(userIdKey).set(userId);
        userChannel.setChannelContext(ctx.channel());
    }

}
