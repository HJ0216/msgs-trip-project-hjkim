package com.msgs.domain.tripstory.service;

import com.msgs.domain.tripstory.dto.StoryBlockDTO;
import com.msgs.domain.tripstory.dto.StoryCommentDTO;
import com.msgs.domain.tripstory.dto.StoryResponseDTO;
import com.msgs.domain.tripstory.dto.TripStoryMainDTO;
import java.util.List;
import java.util.Map;

public interface TripStoryService {

  // 이야기 상세페이지 내용 가져오기
  StoryResponseDTO getStoryDetail(int storyId);

  // 이야기 상세 댓글
  List<StoryCommentDTO> getCommentList(int storyId);

  void commentInsert(StoryCommentDTO storyCommentDTO);


  Boolean saveStory(Map<String, Object> storyData, List<String> dateList,
      Map<Integer, String> dailyComment, Map<Integer, List<StoryBlockDTO>> storyList);

  // 이야기 목록 가져오기
  List<TripStoryMainDTO> getStoryList();
}

