package com.msgs.domain.user.dto;

import com.msgs.domain.user.domain.LoginType;
import com.msgs.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

  public static UserDTO toUserDTO(User u) {
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
