package cn.linked.link.entity;

import io.netty.channel.Channel;
import lombok.Data;

import java.lang.ref.WeakReference;

@Data
public class UserChannel {

    private Long userId;
    private WeakReference<Channel> channel;

    public void setChannelContext(Channel channel) {
        this.channel=new WeakReference<>(channel);
    }

    public void close() {
        if(channel!=null) {
            channel.clear();
            channel=null;
        }
    }

    public Channel getChannelContext() {
        return channel.get();
    }

    public Channel getChannel() {
        if(channel != null && channel.get() != null) {
            return channel.get();
        }
        return null;
    }

    public Channel getChannelToUse() {
        if(channel!=null) {
            Channel temp=getChannel();
            if(temp!=null&&temp.isActive()) {
                return temp;
            }
        }
        return null;
    }

    public UserChannel(User user) {
        userId=user.getId();
    }

    public UserChannel(Long userId) {
        this.userId=userId;
    }

}
