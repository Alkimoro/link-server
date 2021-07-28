package cn.linked.link.entity;

import io.netty.channel.Channel;
import lombok.Data;

import java.lang.ref.WeakReference;

@Data
public class UserChannel {

    private Long userId;
    private WeakReference<Channel> channel;

    public void setChannel(Channel channel) {
        this.channel=new WeakReference<>(channel);
    }

    public void removeChannel() {
        if(channel!=null) {
            channel.clear();
            channel=null;
        }
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
