package com.msgs.domain.tripstory.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msgs.domain.tripstory.dto.StoryLikeCountDTO;
import com.msgs.domain.tripstory.service.TripStoryService;

@RestController // JSON 또는 XML 형식의 데이터를 반환
@RequestMapping("/destination")
@RequiredArgsConstructor // final variable에 대한 생성자 생성
public class TripStoryController3 {

	private final TripStoryService tripStoryService;
	
	// 회원 가입
    @PostMapping("/getStorylike")
    public void storyLike(@RequestBody StoryLikeCountDTO storyLikeCountDTO) {
        tripStoryService.storyLike(storyLikeCountDTO);
    }
	
	
}
