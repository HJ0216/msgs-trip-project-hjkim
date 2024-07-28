package com.msgs.user.service;

import com.msgs.msgs.dto.UserDTO;
import com.msgs.msgs.entity.user.User;

public interface UserService {


    public void signUp(User user);

	public UserDTO getUserInfo(String email);

	public UserDTO getUser(String id);
    
}
