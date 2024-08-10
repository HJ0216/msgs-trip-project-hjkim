package com.msgs.user.service;

import com.msgs.msgs.entity.user.User;
import com.msgs.msgs.error.BusinessException;
import com.msgs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.msgs.msgs.error.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService2 {

    private final UserRepository userRepository;

    public void emailDuplicateCheck(String email){
        if(!userRepository.findByEmail(email).isEmpty()){
            throw new BusinessException(DUPLICATED_EMAIL);
        }
    }

    @Transactional
    public Integer create(User user){
        emailDuplicateCheck(user.getEmail());
        userRepository.save(user);

        return user.getId();
    }
}
