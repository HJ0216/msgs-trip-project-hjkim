package com.msgs.mypage.service;

import java.util.Optional;

import java.util.ArrayList;
import java.util.List;

import com.msgs.msgs.dto.MyPageReviewDTO;
import com.msgs.msgs.dto.MyPageScheduleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.msgs.msgs.dto.TripDTO;
import com.msgs.msgs.dto.TripStoryMainDTO;
import com.msgs.msgs.dto.UserDTO;
import com.msgs.mypage.dao.MyPageDAO;
import com.msgs.msgs.entity.user.User;
import com.msgs.mypage.dto.MyPageUserDTO;
import com.msgs.user.dao.UserDAO;

@Service
public class MyPageServiceImpl implements MyPageService {

	@Autowired
	private MyPageDAO myPageDAO;

	@Autowired
	private UserDAO userDAO;


	// 유저 정보 불러오기
//	@Override
//	public MyPageUserDTO getMyInfo(String userId) {
//		MyPageUserDTO myInfo = myPageDAO.findMyInfo(userId);
//		
//	System.out.println(userEntity);
//        if (userEntity.isPresent()) {
//            UserEntity resultUserEntity = userEntity.get();
//            MyPageUserDTO myPageUserDTO = new MyPageUserDTO(resultUserEntity);
//
//            System.out.println("myPageUserDTO 데이터 있음 ");
//            return myPageUserDTO;
//            
//        }
//        
//        System.out.println("myPageUserDTO 비어있음 ");
//
//        return myInfo;
//	}

//	// 회원 탈퇴
//	@Override
//	public void userDelete(String userId) {
//		myPageDAO.deleteById(userId);
//	}

//	@Override
//	public void insertImgPath(String imagePath, String userId) {
//		MyPageUserDTO userDTO = new MyPageUserDTO();
//		userDTO.setImgPath(imagePath);
//		myPageDAO.save(userDTO);
//	}

//	@Override
//	public void insertImagePath(String imagePath) {
//		MyPageUserDTO userDTO = new MyPageUserDTO();
//		userDTO.setImgPath(imagePath);
//		myPageDAO.save(userDTO);
//	}

	// trip List 조회
	@Override
	public List<TripDTO> tripListAll(String id) {
//        List<Object[]> queryResult = myPageDAO.findAllWithTripAndDetail();
		List<TripDTO> resultList = new ArrayList<>(); // 반환받을 DTO
		return resultList;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	// ========== 유저 정보 불러오기 ==========
	@Override
	public MyPageUserDTO getMyInfo(String userId) {
		MyPageUserDTO myInfo = new MyPageUserDTO();
//		MyPageUserDTO myInfo = myPageDAO.findMyInfo(userId);
		return myInfo;
	}
	
	// ========== 유저 정보 업데이트 ==========
	@Override
	public void updateMyInfo(String id) {
//		List<Object[]> queryResult = myPageDAO.findUserEntity(id);
		List<Object[]> queryResult = new ArrayList<>();

		User user = (User) queryResult.get(0)[0];

		
	}
	
	// ========== 유저 정보 삭제 ==========
	@Override
	public void deleteMyInfo(String id) {
//		MyPageUserDTO myInfo = myPageDAO.findMyInfoForDelete(id);
//	    myInfo.setUserEmail("");
//	    myInfo.setUserPhone("");
//	    myInfo.setUserPwd("");
//	    myInfo.setUserName("");
//	    myInfo.setUserGender("");
//	    myInfo.setRegDate("");
//	    myInfo.setModDate("");
//	    myInfo.setLocationConsent("");
//	    myInfo.setRegUser("");
//	    myInfo.setProfileImage("");
//	    
//	    myInfo.setImgOriginName("");
//	    myInfo.setImgPath("");
//	    myInfo.setImgRegDate(null);
//		myPageDAO.save(id);
	}
	
	

	// ========== 여행 일정 불러오기 ==========
	@Override
	public List<MyPageScheduleDTO> getScheduleList(String id) {
//		List<MyPageScheduleDTO> scheduleList = myPageDAO.findMyPageTripSchedule(id);
		List<MyPageScheduleDTO> scheduleList = new ArrayList<>();
		System.out.println(scheduleList.size());
//		for (int i = 0; i < scheduleList.size(); i++) {
//			int cnt = myPageDAO.countMyPageTripSchedule(id, scheduleList.get(i).getTripId());
//			System.out.println(cnt);
//			scheduleList.get(i).setPlaceCnt(cnt);
//
//		}
		return scheduleList;
	}

	// ========== 유저 이미지 불러오기 ==========
	@Override
	public UserDTO getProfile(String id) {
		Optional<User> userEntity = userDAO.findById(Integer.parseInt(id));

		if (userEntity.isPresent()) {
			User resultUser = userEntity.get();
			UserDTO userDTO = new UserDTO();
			userDTO.setImagePath(resultUser.getImagePath());

			System.out.println("=======getProfile===========" + resultUser.getImagePath());

			return userDTO;
		}
		return null;
	}

	@Override
	public List<TripStoryMainDTO> getStoryList(String id) {
		return null;
	}

	// ========== 리뷰 리스트 불러오기 ==========
	@Override
	public List<MyPageReviewDTO> getReviewList(String id) {
		List<MyPageReviewDTO> getReviewList = new ArrayList<>();
//		List<MyPageReviewDTO> getReviewList = myPageDAO.findMyPageTripLocReview(id);

		return getReviewList;
	}

}