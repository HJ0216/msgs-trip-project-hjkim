package com.msgs.user.controller;


import com.msgs.msgs.dto.UserDTO;
import com.msgs.msgs.entity.user.User;
import com.msgs.user.service.SmsService;
import com.msgs.user.service.UserService;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;


@RestController
@RequestMapping("user")
public class UserController {


    @Autowired
    private UserService userService;
    @Autowired
    private SmsService smsService;


    
    @PostMapping("/signup/smscheck")
	public String smsCheck(@RequestBody String phone) throws ParseException{

//    	String verify = memberService.getMember(phone); // duplicate check
        
//        if (verify.equalsIgnoreCase("exist")) {
//            return "exist";
//        } else {
            Random random = new Random();
            String numStr = "";
            for (int i = 0; i < 6; i++) {
                String ran = Integer.toString(random.nextInt(10));
                numStr += ran;
            }
            smsService.sendSms(phone, numStr); //send authentication number

            return numStr;
//        }
    }


    // 회원가입
//    @PostMapping("/signup")
//    public void userSignUp(@RequestBody User user) {
//    	System.out.println("=================email=============" + user.getEmail());
//        userService.signUp(user);
//    }
    
    // 회원 정보 검색(이메일)
    @PostMapping("/getUserInfo")
    public UserDTO getUserInfo(@RequestParam("email") String email) {
       System.out.println(email);
       return userService.getUserFromEmail(email);
    }
    
    // 회원 정보 검색(userId)
    @PostMapping("/getUser")
    public UserDTO getUser(@RequestParam("id") String id) {
    	System.out.println(id);
    	return userService.getUserFromId(Integer.parseInt(id));
    }


}

