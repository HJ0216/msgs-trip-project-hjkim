package com.msgs.tripstory.service;


import com.msgs.msgs.dto.StoryBlockDTO;

import com.msgs.msgs.dto.StoryResponseDTO;

import com.msgs.msgs.entity.schedule.Trip;
import com.msgs.tripschedule.dao.TripScheduleDAO;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.msgs.msgs.dto.StoryCommentDTO;
import com.msgs.msgs.dto.TripStoryMainDTO;

import com.msgs.tripstory.dto.StoryLikeCountDTO;


import com.msgs.msgs.entity.user.User;
import com.msgs.user.dao.UserDAO;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TripStoryServiceImpl implements TripStoryService {
	
    @Autowired
    private UserDAO userDAO;

	@Autowired
	private TripScheduleDAO scheduleDAO;



	@Override
	public StoryResponseDTO getStoryDetail(int storyId) {

		StoryResponseDTO storyResponseDTO = new StoryResponseDTO();
		Map<String, Object> storyData = new HashMap<>();

		return storyResponseDTO;
	}





	@Override
	@Transactional
	//storyList(tripStoryCreate 페이지에서 입력한 여행기) 저장
	public Boolean saveStory(
		Map<String, Object> storyData,
		List<String> dateList,
		Map<Integer, String> dailyComment,
		Map<Integer, List<StoryBlockDTO>> storyList) {

		System.out.println("s11111111111111111111111111111111111111111111111111111111111111111111111");

		/*TRIP_STORY 엔티티에 저장*/
		Optional<User> userEntity = userDAO.findById(1); // id 이용해서 UserEntity 엔티티 가져오기 */

		//UserEntity resultUserEntity = userEntity.get();

	//	if (!userEntity.isPresent()) {
	//		return false;
	//	}
System.out.println(userEntity);
System.out.println(userEntity.get());
		User resultUser = userEntity.get();


		

		Optional<Trip> scheduleEntity = scheduleDAO.findById(
			Integer.parseInt(storyData.get("schedule_id").toString())
		); // schedule_id 이용해서 SchduleEntity 엔티티 가져오기 */

		Trip resultScheduleEntity = scheduleEntity.get();

		System.out.println("S333333333333333333333333333333333333333333333333333333333333333");

		System.out.println(resultUser.getId());
		System.out.println(resultScheduleEntity.getId());

		return true;
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
	public List<TripStoryMainDTO> getStoryList() {
        List<TripStoryMainDTO> resultList = new ArrayList<>(); // 반환받을 DTO
        
		return resultList;

	}


}
