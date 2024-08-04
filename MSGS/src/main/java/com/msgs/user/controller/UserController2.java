package com.msgs.user.controller;

import com.msgs.msgs.entity.user.User;
import com.msgs.user.service.UserService2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v2/users")
@RequiredArgsConstructor
public class UserController2 {
    private final UserService2 userService;
    @PostMapping("/new")
    public String create(@RequestBody User user){
        System.out.println("UserController2.create");

        Integer id = userService.create(user);
        return id.toString();
    }
}
