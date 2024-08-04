package com.msgs.tripplace.dao;

import com.msgs.msgs.entity.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripPlaceDAO extends JpaRepository<Review, String> {

//	@Query("SELECT new com.msgs.msgs.dto.TripPlaceReviewDTO(pr, u) " +
//	        "FROM Review pr " +
//	        "JOIN pr.user u " +
//	        "WHERE pr.contentId = :contentId " +
//	        "ORDER BY pr.regDate DESC, pr.id DESC")
//	List<TripPlaceReviewDTO> findAllWithUserOrderDate(@Param("contentId") String contentId);
	
//	@Query("SELECT new com.msgs.msgs.dto.TripPlaceReviewDTO(pr, u) " +
//	        "FROM Review pr " +
//	        "JOIN pr.user u " +
//	        "WHERE pr.contentId = :contentId " +
//	        "GROUP BY pr, u " +
//	        "ORDER BY pr.regDate DESC, pr.id DESC")
//	List<TripPlaceReviewDTO> findAllWithUserOrderLike(@Param("contentId") String contentId);
//
//	// 유저별 작성 리뷰수 조회
//	@Query("SELECT COUNT(pr) FROM Review pr WHERE pr.user.id = :userId")
//    int getUserReviewCount(@Param("userId") String userId);
//
//	// 리뷰 이미지 리스트 조회
//	@Query("SELECT pri " +
//	        "FROM Review pr " +
//	        "JOIN pr.reviewImages pri " +
//	        "WHERE pr.id = :reviewId")
//	List<ReviewImage> findImgListById(@Param("reviewId") int reviewId);
//
//	// 리뷰 이미지 저장
////  @Transactional
////	@Modifying
//	@Query(value = "INSERT INTO place_review_img (review_id, img_path) " +
//	               "VALUES (:reviewId, :imgPath)", nativeQuery = true)
////	void imgSave(@Param("reviewId") int reviewId, @Param("imgOriginName") String imgOriginName,
////            @Param("imgServerName") String imgServerName, @Param("imgPath") String imgPath);
//	void imgSave(@Param("reviewId") int reviewId, @Param("imgPath") String imgPath);
}

