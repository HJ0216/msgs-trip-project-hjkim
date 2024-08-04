package com.msgs.main.service;

import com.msgs.msgs.entity.user.User;
import com.msgs.user.repository.UserRepository;
import com.msgs.user.service.UserService2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    UserService2 userService;
    @Autowired
    UserRepository userRepository;

    @Test
    public void 회원가입() throws Exception {
        // given
        User user = new User();
        user.setStatus("M");
        user.setEmail("test@email.com");
        user.setPhone("010-2345-6789");

        // when
        Integer createdId = userService.create(user);

        // then
        assertThat(user).isEqualTo(userRepository.findOne(createdId));
    }
}
