package com.msgs.domain.user.dto;

import lombok.Data;

@Data
public class UserLoginRequestDto {
//    private String id;
    private String email;
    private String password;
}
