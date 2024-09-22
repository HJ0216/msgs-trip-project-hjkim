package com.msgs.global.common.jwt;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenInfo {

  private String grantType;
  @NotEmpty
  private String accessToken;
  private String refreshToken;
}
