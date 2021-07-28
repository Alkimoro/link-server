package cn.linked.link.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String defaultMongoConnectionString;

    @Value("${spring.data.mongodb.autoInc.uri}")
    private String autoIncMongoConnectionString;

    @Primary
    public @Bean(name = "defaultMongoTemplate") MongoTemplate getDefaultMongoTemplate() {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(defaultMongoConnectionString);
        return new MongoTemplate(factory);
    }

    public @Bean(name = "autoIncMongoTemplate") MongoTemplate getAutoIncMongoTemplate() {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(autoIncMongoConnectionString);
        return new MongoTemplate(factory);
    }

}
