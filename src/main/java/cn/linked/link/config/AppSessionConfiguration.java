package cn.linked.link.config;

import cn.linked.link.component.AppSessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppSessionConfiguration {

    @Bean
    public AppSessionRepository sessionRepository() {
        return new AppSessionRepository();
    }

}
