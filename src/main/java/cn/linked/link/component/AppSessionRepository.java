package cn.linked.link.component;

import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSessionRepository;

public class AppSessionRepository extends MapSessionRepository {

    @Override
    public ExpiringSession createSession() {
        ExpiringSession session=super.createSession();
        session=new AppSession(session);
        return session;
    }

    @Override
    public AppSession getSession(String id) {
        ExpiringSession session = super.getSession(id);
        if(session != null) {
            return new AppSession(session);
        }
        return null;
    }
}
