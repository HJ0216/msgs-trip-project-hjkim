package com.msgs.domain.tripstory.service;

import org.springframework.stereotype.Service;

import com.msgs.domain.tripstory.dto.StoryBlockDTO;
import com.msgs.domain.tripstory.dto.StoryResponseDTO;
import com.msgs.domain.tripstory.dto.StoryCommentDTO;
import com.msgs.domain.tripstory.dto.TripStoryMainDTO;
import com.msgs.domain.tripstory.dto.StoryLikeCountDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
public class TripStoryServiceImpl implements TripStoryService {

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
