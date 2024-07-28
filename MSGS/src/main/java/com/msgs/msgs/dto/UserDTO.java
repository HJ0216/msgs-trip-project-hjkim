package com.msgs.msgs.dto;

import com.msgs.msgs.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class UserDTO {
	
    // UserEntity
    private Integer id;
    private String status;
    private String email;
    private String phone;
    private String nickname;
    private String password;
    private String imagePath;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public UserDTO(User user) {
        this.id = user.getId();
        this.status = user.getStatus();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.imagePath = user.getImagePath();
        this.createdDate = user.getCreatedDate();
        this.updatedDate = user.getUpdatedDate();
    }
}
