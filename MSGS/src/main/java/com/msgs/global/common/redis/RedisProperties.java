package com.msgs.global.common.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis")
// application.yml 또는 application.properties 파일에서 spring.redis로 시작하는 설정 값을 이 클래스의 필드에 바인딩
@Getter
@Setter
public class RedisProperties {

  private int port;
  private String host;
}