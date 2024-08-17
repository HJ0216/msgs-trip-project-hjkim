package com.msgs.msgs.dto;

import com.msgs.msgs.entity.user.LoginType;
import com.msgs.msgs.entity.user.User;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
	
    // UserEntity
    private Integer id;
    private String status;
    private LoginType loginType;
    private String role;
    private String email;
    private String phone;
    private String nickname;
    private String password;
    private String imagePath;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static UserDTO toUserDTO(User u){
        return UserDTO.builder()
                .id(u.getId())
                .status(u.getStatus())
                .loginType(u.getLoginType())
                .role(u.getRole())
                .email(u.getEmail())
                .phone(u.getPhone())
                .nickname(u.getNickname())
                .password(u.getPassword())
                .imagePath(u.getImagePath())
                .createdDate(u.getCreatedDate())
                .updatedDate(u.getUpdatedDate())
                .build();
    }

}
