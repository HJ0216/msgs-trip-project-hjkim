package com.msgs.msgs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LogoutRequestDTO {
    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;

    public static LogoutRequestDTO of(String accessToken, String refreshToken) {
        LogoutRequestDTO logoutRequestDto = new LogoutRequestDTO();
        logoutRequestDto.accessToken = accessToken;
        logoutRequestDto.refreshToken = refreshToken;
        return logoutRequestDto;
    }
}
