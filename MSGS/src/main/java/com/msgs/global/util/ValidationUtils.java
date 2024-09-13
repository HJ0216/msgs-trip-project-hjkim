package com.msgs.global.util;

import com.msgs.global.common.error.BusinessException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.msgs.global.common.error.ErrorCode.*;

public class ValidationUtils {
    // A utility class is a class that only has static members, hence it should not be instantiated
    private ValidationUtils() {
        throw new IllegalStateException("Utility class");
    }
    private static final Pattern emailPattern = Pattern.compile("(\\w\\.)*\\w+@[\\w.-]+\\.[A-Za-z]{2,3}");
    private static final Pattern passwordPattern = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*[!@#$%^&*()-_+=])(?=.*\\d).{8,20}$");
    // 적어도 하나 이상의 영문자, 특수문자, 숫자가 포함
    // 비밀번호가 8자 이상 20자 이하
    private static final Pattern nicknamePattern = Pattern.compile("^[A-Za-zㄱ-힣]{2,10}$");
    // 영문자 또는 한글로 시작
    // 닉네임이 2자에서 10자 사이
    private static final Pattern phoneNumberPattern = Pattern.compile("^01([0|1|6|7|8|9])(\\d{3}|\\d{4})\\d{4}$");

    public static void validateEmail(String email) {
        Matcher matcher = emailPattern.matcher(email);

        if (!matcher.matches()) {
            throw new BusinessException(EMAIL_VALIDATION);
        }
    }

    public static void validatePassword(String password) {
        Matcher matcher = passwordPattern.matcher(password);

        if (!matcher.matches()) {
            throw new BusinessException(PASSWORD_VALIDATION);
        }
    }

    public static void validateNickname(String nickname) {
        Matcher matcher = nicknamePattern.matcher(nickname);

        if (!matcher.matches()) {
            throw new BusinessException(NICKNAME_VALIDATION);
        }
    }

    public static void validatePhoneNumber(String phoneNumber) {
        Matcher matcher = phoneNumberPattern.matcher(phoneNumber);

        if (!matcher.matches()) {
            throw new BusinessException(PHONE_NUMBER_VALIDATION);
        }
    }

    public static void confirmPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(PASSWORD_CONFIRM_VALIDATION);
        }
    }
}