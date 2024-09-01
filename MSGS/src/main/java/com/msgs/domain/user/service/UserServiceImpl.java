package com.msgs.domain.user.service;

import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Override
    public String create(User user) {
        return null;
    }

    @Override
    public UserDTO getUserFromEmail(String email) {
        return null;
    }

    @Override
    public UserDTO getUserFromId(Integer id) {
        return null;
    }
}

