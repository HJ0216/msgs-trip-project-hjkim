package com.msgs.domain.user.domain;

public enum UserType {
  GOOGLE,
  KAKAO,
  MSGS,
  NAVER;

  public static boolean isValidUserType(String type) {
    try {
      UserType.valueOf(type.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}