package com.msgs.user.service;

import com.msgs.msgs.dto.UserDTO;
import com.msgs.msgs.entity.user.User;
import com.msgs.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    // 회원가입
    @Override
    public void signUp(User user) {
        userDAO.save(user);
    }

    // 회원 정보 검색(이메일)
	@Override
	public UserDTO getUserInfo(String email) {
		
		System.out.println("!!!!!!!!!!!!!!!!!"+email);
        Optional<User> userEntity = userDAO.findByEmail(email);
        // id 제외 findBy 메서드 생성

        if (userEntity.isPresent()) {
            User resultUser = userEntity.get();
            UserDTO userDTO = new UserDTO(resultUser);

            return userDTO;
        }
        
        return null;
	}

	@Override
	public UserDTO getUser(String id) {
        Optional<User> userEntity = userDAO.findById(id);
        // id 제외 findBy 메서드 생성

        if (userEntity.isPresent()) {
            User resultUser = userEntity.get();
            UserDTO userDTO = new UserDTO(resultUser);

            return userDTO;
        }
        
        return null;
	}
}

