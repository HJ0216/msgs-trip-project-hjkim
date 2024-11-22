package com.msgs.domain.user.dto;

import com.msgs.domain.user.domain.User;
import com.msgs.domain.user.domain.UserType;
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

  private Integer id;
  private String status;
  private UserType userType;
  private String role;
  private String email;
  private String phone;
  private String nickname;
  private String password;
  private String imagePath;
  private Boolean isUsed;
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;

  public static UserDTO toUserDTO(User u) {
    return UserDTO.builder()
                  .id(u.getId())
                  .status(u.getStatus())
                  .userType(u.getUserType())
                  .role(u.getRole())
                  .email(u.getEmail())
                  .phone(u.getPhone())
                  .nickname(u.getNickname())
                  .password(u.getPassword())
                  .imagePath(u.getImagePath())
                  .isUsed(u.getIsUsed())
                  .createdDate(u.getCreatedDate())
                  .updatedDate(u.getUpdatedDate())
                  .build();
  }

}
