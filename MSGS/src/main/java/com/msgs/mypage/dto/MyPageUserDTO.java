package com.msgs.mypage.dto;

import com.msgs.msgs.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageUserDTO {

    // 반환 객체 DTO: 필요한 값만 필드로 선언

    // UserDTO
    private String userEmail;
    private String userId;
    private String userPhone;
    private String userPwd;
    private String userName;
    private String userGender;
    private String imgPath;
    private String regDate;
    private String modDate;


    // entity 값 DTO 생성자 주입 - UserEntity
    public MyPageUserDTO(User user) {
        this.userEmail = user.getEmail();
        this.userId = user.getId().toString();
        this.userPhone = user.getPhone();
        this.userPwd = user.getPassword();
        this.userName = user.getNickname();
        this.imgPath = user.getImagePath();
        this.regDate = user.getCreatedDate().toString();
        this.modDate = user.getUpdatedDate().toString();
    }
}