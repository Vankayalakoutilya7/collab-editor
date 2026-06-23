package com.koutilya.collabeditor.config;

import com.koutilya.collabeditor.service.RedisSubscriber;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory connectionFactory() {

        // ✅ Read from ENV (Render)
        String host = System.getenv("SPRING_REDIS_HOST");
        String portStr = System.getenv("SPRING_REDIS_PORT");
        String password = System.getenv("SPRING_REDIS_PASSWORD");

        // 🔥 Fallback for LOCAL (IMPORTANT)
        if (host == null) host = "intent-quail-135082.upstash.io";
if (portStr == null) portStr = "6379";
if (password == null) password = "gQAAAAAAAg-qAAIgcDFjYmJjOTk0Yjc3MDk0ZTMzYTgxNjNhN2ExYjJiYTdhZg";

        int port = Integer.parseInt(portStr);

        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration(host, port);

        config.setPassword(password);

        LettuceClientConfiguration clientConfig =
                LettuceClientConfiguration.builder()
                        .useSsl() // 🔥 REQUIRED
                        .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            RedisSubscriber subscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener((message, pattern) -> {
            subscriber.handleMessage(message.getBody());
        }, new ChannelTopic("document-updates"));

        return container;
    }
    @Bean
    public CommandLineRunner testRedis(RedisTemplate<String, String> redisTemplate) {
        return args -> {
            try {
                redisTemplate.opsForValue().set("test-key", "hello");
                String value = redisTemplate.opsForValue().get("test-key");

                System.out.println("✅ Redis Connected! Value: " + value);
            } catch (Exception e) {
                System.out.println("❌ Redis NOT connected: " + e.getMessage());
            }
        };
    }
}