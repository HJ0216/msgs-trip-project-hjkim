package com.msgs.global.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern emailPattern = Pattern.compile("(\\w\\.)*\\w+@[\\w.-]+\\.[A-Za-z]{2,3}");
    private static final Pattern passwordPattern = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*[!@#$%^&*()-_+=])(?=.*\\d).{8,20}$");
    private static final Pattern nicknamePattern = Pattern.compile("^[A-Za-zㄱ-힣]{2,10}$");
    private static final Pattern phoneNumberPattern = Pattern.compile("^01([0|1|6|7|8|9])(\\d{3}|\\d{4})\\d{4}$");

    public static void validateEmail(String email) {
        Matcher matcher = emailPattern.matcher(email);

        if (!matcher.matches()) {
            // (ExceptionCode.EMAIL_VALIDATION);
        }
    }

    public static void validatePassword(String password) {
        Matcher matcher = passwordPattern.matcher(password);

        if (!matcher.matches()) {
            // (ExceptionCode.PASSWORD_VALIDATION);
        }
    }

    public static void validateName(String name) {
        Matcher matcher = nicknamePattern.matcher(name);

        if (!matcher.matches()) {
            // (ExceptionCode.NAME_VALIDATION);
        }
    }

    public static void validatePhoneNumber(String phoneNumber) {
        Matcher matcher = phoneNumberPattern.matcher(phoneNumber);

        if (!matcher.matches()) {
            // (ExceptionCode.PHONE_NUMBER_VALIDATION);
        }
    }

    public static void confirmPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            // (ExceptionCode.PASSWORD_CONFIRM);
        }
    }
}
