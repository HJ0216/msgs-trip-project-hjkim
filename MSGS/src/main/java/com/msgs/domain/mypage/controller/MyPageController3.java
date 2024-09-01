package com.msgs.domain.mypage.controller;

import com.msgs.domain.mypage.service.MyPageService;

import com.msgs.global.common.jwt.controller.AuthController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mypage")
public class MyPageController3 {

    @Autowired
    private AuthController userController2;

    @Autowired
    private MyPageService myPageService;

//    public JSONObject getUserInfo(String token) {
//        ResponseEntity<?> userInfoResponse = userController2.getUserInfo(token);
//        JSONObject userInfo = new JSONObject((String) userInfoResponse.getBody());
//        return userInfo;
//    }

//    @PostMapping("/profile")
//    public UserDTO getProfile(@RequestBody Map<String, String> tokenValue) {
//    	String token = tokenValue.get("tokenValue");
//        JSONObject userInfo = getUserInfo(token);
//        String id = userInfo.getString("id");
//        return myPageService.getProfile(id);
//    }

//    @PostMapping("/scheduleList")
//    public List<MyPageScheduleDTO> getScheduleList(@RequestBody Map<String, String> tokenValue) {
//    	String token = tokenValue.get("tokenValue");
//        JSONObject userInfo = getUserInfo(token);
//        String id = userInfo.getString("id");
//        return myPageService.getScheduleList(id);
//    }
//
//    @PostMapping("/reviewList")
//    public List<MyPageReviewDTO> getReviewList(@RequestBody Map<String, String> tokenValue) {
//    	String token = tokenValue.get("tokenValue");
//        JSONObject userInfo = getUserInfo(token);
//        String id = userInfo.getString("id");
//        return myPageService.getReviewList(id);
//    }
//
//
//  @PostMapping("/storyList")
//  public List<TripStoryMainDTO> getStoryList(@RequestBody Map<String, String> tokenValue) {
//	  String token = tokenValue.get("tokenValue");
//      JSONObject userInfo = getUserInfo(token);
//      String id = userInfo.getString("id");
//      return myPageService.getStoryList(id);
//  }
  

}