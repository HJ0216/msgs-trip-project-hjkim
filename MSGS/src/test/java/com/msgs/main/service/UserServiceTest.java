package com.msgs.main.service;

import com.msgs.msgs.entity.user.User;
import com.msgs.msgs.error.BusinessException;
import com.msgs.msgs.error.ErrorCode;
import com.msgs.user.repository.UserRepository;
import com.msgs.user.service.UserService2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.msgs.msgs.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    UserService2 userService;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원 가입을 한다.")
    public void userSignUp() throws Exception {
        // given
        User user = new User();
        user.setStatus("M");
        user.setEmail("test@email.com");
        user.setPhone("01023456789");

        // when
        Integer createdId = userService.create(user);

        // then
        assertThat(user).isEqualTo(userRepository.findOne(createdId));
    }

    @Test
    @DisplayName("이메일이 동일한 회원이 존재할 경우, 예외가 발생한다.")
    public void emailDuplicateCheck() throws Exception {
        // given
        User userA = new User();
        userA.setStatus("M");
        userA.setEmail("test@email.com");
        userA.setPhone("01023456789");
        userService.create(userA);

        String existingEmail = "test@email.com";

        // when

        // then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.emailDuplicateCheck(existingEmail));
        assertEquals(DUPLICATED_EMAIL, exception.getErrorCode());
    }
}
