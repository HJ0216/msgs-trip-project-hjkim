package com.msgs.msgs.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import com.msgs.msgs.entity.review.Review;
import com.msgs.msgs.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripPlaceReviewDTO {
	
	// PLACE_REVIEW
	private int reviewId;
	private Integer userId;
	private String comments;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	
	// PLACE_REVIEW_IMAGE
	// 이미지 넣을 때
	private List<Object> base64List;
	private int imgSeq;
	private String imgOriginName;
	private String imgServerName;
	private String imgPath;
	// 이미지 꺼내올 때
	private List<HashMap<String, String>> reviewImgList; 
	
	// USER
    private String userName;
    private int userReviewCnt;
    
    // USER_IMAGE
    private String userImgPath;
    
    // DTO 생성자로 entity 값 주입 - 리뷰 이미지 제외
//	public TripPlaceReviewDTO(Review destinationReview, UserEntity userEntity, UserImg userImg, Long userReviewCnt) {
	public TripPlaceReviewDTO(Review review, User user) {
	    this.reviewId = review.getId();
	    this.comments = review.getComments();
	    this.createdDate = review.getCreatedDate();
	    this.updatedDate = review.getUpdatedDate();

	    this.userName = user.getNickname();
	    this.userImgPath = user.getImagePath();
	}
	
	// DTO 생성자로 entity 값 주입 - 리뷰 이미지
	public TripPlaceReviewDTO(int reviewId, HashMap<String, String> reviewImg) {
		this.reviewId = reviewId;
//		this.imgOriginName;
//		this.imgServerName;
		this.imgPath = reviewImg.get("imgPath");
	}
}
