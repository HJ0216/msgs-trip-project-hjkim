package com.msgs.user.service;

import com.msgs.msgs.entity.user.User;
import com.msgs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService2 {

    private final UserRepository userRepository;

    @Transactional
    public Integer create(User user){
//        validateDuplicateUser(user);
        userRepository.save(user);

        return user.getId();
    }
}
