package cn.linked.link;

import cn.linked.link.socket.ChatServer;
import lombok.Data;
import lombok.Getter;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Data
@MapperScan("cn.linked.link.dao")
@EnableTransactionManagement
@EnableSpringHttpSession
@SpringBootApplication
public class LinkApplication {

    @Value("${http.port}")
    private int httpPort;
    @Value("${server.port}")
    private int serverPort;

    @Getter
    private static ApplicationContext applicationContext;

    private static ChatServer chatServer;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(LinkApplication.class, args);
        chatServer = applicationContext.getBean(ChatServer.class);
        //启动服务端
        chatServer.start();
    }

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory () {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    public Connector httpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setRedirectPort(serverPort);
        return connector;
    }

}
