package cn.linked.link.component;

import cn.linked.link.entity.User;
import io.netty.util.AttributeKey;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.session.ExpiringSession;

import java.util.Set;

public class AppSession implements ExpiringSession {

    @Setter
    private ExpiringSession session;

    public AppSession(@NonNull ExpiringSession session) {
        this.session=session;
    }

    @Override
    public long getCreationTime() {
        return session.getCreationTime();
    }

    @Override
    public void setLastAccessedTime(long lastAccessedTime) {
        session.setLastAccessedTime(lastAccessedTime);
    }

    @Override
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    @Override
    public void setMaxInactiveIntervalInSeconds(int interval) {
        session.setMaxInactiveIntervalInSeconds(interval);
    }

    @Override
    public int getMaxInactiveIntervalInSeconds() {
        return session.getMaxInactiveIntervalInSeconds();
    }

    @Override
    public boolean isExpired() {
        return session.isExpired();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public <T> T getAttribute(String attributeName) {
        return session.getAttribute(attributeName);
    }

    @Override
    public Set<String> getAttributeNames() {
        return session.getAttributeNames();
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        session.setAttribute(attributeName,attributeValue);
    }

    public Long getUserId() {
        return this.getAttribute(User.STRING_KEY_ID);
    }

    @Override
    public void removeAttribute(String attributeName) {
        session.removeAttribute(attributeName);
    }
}
