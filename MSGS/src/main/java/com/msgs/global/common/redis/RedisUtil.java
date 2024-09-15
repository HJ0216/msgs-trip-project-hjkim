package com.msgs.global.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;
    // Redis와의 기본적인 데이터 상호작용을 처리하는 객체
    // 일반 데이터를 저장하고 조회하는 데 사용
    private final RedisTemplate<String, Object> redisBlackListTemplate;

    public void set(String key, Object o, long milliseconds) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(o.getClass()));
        redisTemplate.opsForValue().set(key, o, milliseconds, TimeUnit.MILLISECONDS);
        // Redis에 데이터를 저장
        // key: Redis에 저장할 때 사용할 키 값
        // o: 저장할 객체
        // opsForValue: String을 쉽게 Serialize / Deserialize 해주는 interface
        // 자바 언어에서 사용되는 Object 또는 Data를 다른 컴퓨터의 자바 시스템에서도 사용 할수 있도록 바이트 스트림(stream of bytes) 형태로 연속전인(serial) 데이터로 변환하는 포맷 변환 기술
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    // 특정 키가 Redis에 존재하는지 확인
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setBlackList(String key, Object o, long milliseconds) {
        redisBlackListTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(o.getClass()));
        redisBlackListTemplate.opsForValue().set(key, o, milliseconds, TimeUnit.MILLISECONDS);
    }

    public Object getBlackList(String key) {
        return redisBlackListTemplate.opsForValue().get(key);
    }

    public boolean deleteBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.delete(key));
    }

    public boolean hasKeyBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(key));
    }
}