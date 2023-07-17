package com.gigajet.mhlb.config;

import com.gigajet.mhlb.global.subscriber.ChatAlarmSubscriber;
import com.gigajet.mhlb.global.subscriber.ChatSubscriber;
import com.gigajet.mhlb.global.subscriber.StatusSubscriber;
import com.gigajet.mhlb.global.subscriber.WorkspaceInviteAlarmSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    /*
        redis의 pub/sub 기능을 이용하기 위해 MessageListener 설정 추가
        메시지 발행이 오면 Listener가 처리함
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            @Qualifier("chatMessageListenerAdapter") MessageListenerAdapter chatMessageListenerAdapter,
            @Qualifier("statusMessageListenerAdapter") MessageListenerAdapter handleMessageListenerAdapter,
            @Qualifier("chatAlarmMessageListenerAdapter") MessageListenerAdapter chatAlarmMessageListenerAdapter,
            @Qualifier("workspaceInviteAlarmMessageListenerAdapter") MessageListenerAdapter workspaceInviteAlarmMessageListenerAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // RedisMessageListenerContainer 에 Bean 으로 등록한 listenerAdapter, channelTopic 추가
        container.addMessageListener(chatMessageListenerAdapter, new ChannelTopic("chatMessageChannel"));
        container.addMessageListener(handleMessageListenerAdapter, new ChannelTopic("statusMessageChannel"));
        container.addMessageListener(chatAlarmMessageListenerAdapter, new ChannelTopic("chatAlarmMessageChannel"));
        container.addMessageListener(workspaceInviteAlarmMessageListenerAdapter, new ChannelTopic("workspaceInviteAlarmMessageChannel"));
        return container;
    }

    @Bean
    public MessageListenerAdapter chatMessageListenerAdapter(ChatSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    public MessageListenerAdapter statusMessageListenerAdapter(StatusSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    public MessageListenerAdapter chatAlarmMessageListenerAdapter(ChatAlarmSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    public MessageListenerAdapter workspaceInviteAlarmMessageListenerAdapter(WorkspaceInviteAlarmSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }
}