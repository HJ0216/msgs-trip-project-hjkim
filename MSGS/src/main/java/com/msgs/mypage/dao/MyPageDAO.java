package com.msgs.mypage.dao;


import java.util.List;

import com.msgs.msgs.dto.MyPageReviewDTO;
import com.msgs.msgs.dto.MyPageScheduleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.msgs.msgs.entity.user.User;
import com.msgs.mypage.dto.MyPageUserDTO;

@Repository
public interface MyPageDAO extends JpaRepository<User, String>{

    @Query("SELECT new com.msgs.msgs.dto.MyPageScheduleDTO(u, ts) " +
            "FROM User u " +
            "JOIN u.trip ts " +
            "WHERE u.id = :id")
    List<MyPageScheduleDTO> findMyPageTripSchedule(@Param("id") String id);

    // 메소드 시그니처와 JPQL 쿼리를 사용하여 MyPageScheduleDTO 리스트를 가져오는 메소드
    @Query("SELECT count(*)" +
            "FROM User u " +
            "JOIN u.trip ts " +
            "JOIN ts.tripDays tds " +
            "JOIN tds.dayDestinations tdsDestination " +
            "WHERE u.id = :id and ts.id = :scheduleId")
    int countMyPageTripSchedule(@Param("id") String id, @Param("scheduleId") int scheduleId);

    @Query("SELECT new com.msgs.msgs.dto.MyPageReviewDTO(u, pr) " +
            "FROM User u " +
            "JOIN u.placeReviews pr " +
            "WHERE u.id = :id")
    List<MyPageReviewDTO> findMyPageTripLocReview(@Param("id") String id);
    
    @Query("SELECT new com.msgs.mypage.dto.MyPageUserDTO(u) " +
            "FROM User u " +
            "WHERE u.id = :id")
    MyPageUserDTO findMyInfo(@Param("id") String id);
    
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.id = :id")
    List<Object[]> findUserEntity(@Param("id") String id);    

//    @Query("SELECT new com.msgs.mypage.dto.MyPageUserDTO(ue, ui) " +
//            "FROM User u " +
//            "JOIN u.userImg ui " +
//            "WHERE u.id = :id")
//    MyPageUserDTO findMyInfoForDelete(@Param("id") String id);
}
