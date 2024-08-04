package com.msgs.user.service;

import com.msgs.msgs.dto.UserDTO;
import com.msgs.msgs.entity.user.User;

public interface UserService {

    public String create(User user);

	public UserDTO getUserFromEmail(String email);

	public UserDTO getUserFromId(Integer id);
    
}
