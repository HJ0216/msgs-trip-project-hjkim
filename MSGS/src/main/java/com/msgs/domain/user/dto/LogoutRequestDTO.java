package com.msgs.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LogoutRequestDTO {
    @NotEmpty
    private String accessToken;

    @NotEmpty
    private String refreshToken;

    public static LogoutRequestDTO of(String accessToken, String refreshToken) {
        LogoutRequestDTO logoutRequestDto = new LogoutRequestDTO();
        logoutRequestDto.accessToken = accessToken;
        logoutRequestDto.refreshToken = refreshToken;
        return logoutRequestDto;
    }
}
