package com.msgs.domain.user.service;

import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.domain.User;

public interface UserService {

    public String create(User user);

	public UserDTO getUserFromEmail(String email);

	public UserDTO getUserFromId(Integer id);
    
}
