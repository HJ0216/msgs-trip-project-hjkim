package com.msgs.domain.user.domain;

import com.msgs.global.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Integer id;

  @Column(nullable = false, columnDefinition = "char(1)")
  private String status;

  @Enumerated(EnumType.STRING)
  private UserType userType;

  @Column(nullable = false)
  private String role;

  @Column(nullable = false, unique = true, length = 50)
  private String email;

  @Column(nullable = false, unique = true, columnDefinition = "char(11)")
  private String phone;

  @Column(length = 30)
  private String nickname;

  @Column(length = 60)
  private String password;

  private String imagePath;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User user)) {
      return false;
    }
    return Objects.equals(getId(), user.getId()) && Objects.equals(getStatus(), user.getStatus())
        && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getPhone(),
        user.getPhone());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getStatus(), getEmail(), getPhone());
  }
}