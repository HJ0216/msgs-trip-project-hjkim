package com.msgs.global.config;

import com.msgs.global.common.redis.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
// 스프링의 설정 파일
// 하나 이상의 @Bean 메서드를 포함하고 있으며, 스프링 컨테이너가 이 메서드들을 관리하고 인스턴스를 생성
@EnableRedisRepositories
// 스프링 데이터 Redis 리포지토리를 활성화
// Redis와 상호작용하는 리포지토리 인터페이스를 자동으로 스프링 컨테이너가 인식
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 애플리케이션이 Redis에 연결할 때 그 연결을 만드는 방법을 정의한 것
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
        // Lettuce 클라이언트를 사용해 실제 Redis 서버에 연결을 설정하고 관리
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // Redis 서버에 데이터를 저장하거나 가져오는 데 사용
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
