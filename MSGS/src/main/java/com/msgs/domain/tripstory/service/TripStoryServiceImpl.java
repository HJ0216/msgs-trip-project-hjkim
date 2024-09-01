package com.msgs.domain.tripstory.service;


import com.msgs.domain.tripstory.dto.StoryBlockDTO;

import com.msgs.domain.tripstory.dto.StoryResponseDTO;

import com.msgs.domain.tripschedule.domain.Trip;
import com.msgs.domain.tripschedule.dao.TripScheduleDAO;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.msgs.domain.tripstory.dto.StoryCommentDTO;
import com.msgs.domain.tripstory.dto.TripStoryMainDTO;

import com.msgs.domain.tripstory.dto.StoryLikeCountDTO;


import com.msgs.domain.user.domain.User;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TripStoryServiceImpl implements TripStoryService {
	
	@Autowired
	private TripScheduleDAO scheduleDAO;



	@Override
	public StoryResponseDTO getStoryDetail(int storyId) {

		StoryResponseDTO storyResponseDTO = new StoryResponseDTO();
		Map<String, Object> storyData = new HashMap<>();

		return storyResponseDTO;
	}



	@Override
	public List<StoryCommentDTO> getCommentList(int storyId) {

        List<StoryCommentDTO> resultList = new ArrayList<>(); // 반환받을 DTO

		return resultList;
	}

	@Override
	public void storyLike(StoryLikeCountDTO storyLikeCountDTO) {

		storyLikeCountDTO.setStoryId("");

		storyLikeCountDTO.setUserId("msgs01");
//		tripStoryDAO.save(storyLikeCountDTO);
	}


	@Override
	public void commentInsert(StoryCommentDTO storyCommentDTO) {
	}

	@Override
	public Boolean saveStory(Map<String, Object> storyData, List<String> dateList, Map<Integer, String> dailyComment, Map<Integer, List<StoryBlockDTO>> storyList) {
		return null;
	}


	@Override
	public List<TripStoryMainDTO> getStoryList() {
        List<TripStoryMainDTO> resultList = new ArrayList<>(); // 반환받을 DTO
        
		return resultList;

	}


}
