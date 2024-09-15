package com.msgs.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
public class LogoutRequestDTO {
    @NotEmpty
    private String accessToken;
    @NotEmpty
    private String refreshToken;

    public static LogoutRequestDTO of(String accessToken, String refreshToken) {
        return LogoutRequestDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
