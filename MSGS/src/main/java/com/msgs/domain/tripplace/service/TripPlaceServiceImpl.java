package com.msgs.domain.tripplace.service;

import org.springframework.stereotype.Service;

@Service
public class TripPlaceServiceImpl implements TripPlaceService {
//
//	@Autowired
//	private UserDAO userDAO;
//
//	@Autowired
//	private TripPlaceDAO tripPlaceDAO;
//
//	@Autowired
//	private PlaceReviewImageDAO placeReviewImageDAO;
//
//	@Override
//	public void reviewSubmit(TripPlaceReviewDTO tripPlaceReviewDTO) {
//		System.out.println(tripPlaceReviewDTO);
//		Review review = new Review();
//
//		// userId 이용한 UserEntity 엔티티 반환
//		Optional<User> userEntity = userDAO.findById(tripPlaceReviewDTO.getUserId().toString());
//		if(userEntity.isPresent()) {
//			User resultUser = userEntity.get();
//			review.setUser(resultUser);
//		}
//
//		// seq 값은 자동 생성되므로 set 사용 X
//		review.setComments(tripPlaceReviewDTO.getComments());
//
//		Review newDestinationReview = tripPlaceDAO.saveAndFlush(review);
//
//
//		// 이미지 있을 경우 저장
//		if (!tripPlaceReviewDTO.getReviewImgList().isEmpty()) {
//		    List<HashMap<String, String>> reviewImgList = tripPlaceReviewDTO.getReviewImgList();
//
//		    int index = 1;
//
//		    for (HashMap<String, String> reviewImg : reviewImgList) {
//		    	ReviewImage reviewImage = new ReviewImage();
//		    	reviewImage.setReview(newDestinationReview);
//
//	            System.out.println("서비스임플 이미지 저장: " + reviewImg.get("key" + index));
//	            reviewImage.setImagePath(reviewImg.get("key" + index));
//	            placeReviewImageDAO.saveAndFlush(reviewImage);
//
//	            index++;
//		    }
//		}
//	}
//
//	@Override
//	public List<TripPlaceReviewDTO> getReviewList(Boolean isSortedByLike, String contentId) {
//	    List<TripPlaceReviewDTO> reviewList = new ArrayList<>();
//
//	    if (isSortedByLike) {
////	        reviewList = tripPlaceDAO.findAllWithUserOrderLike(contentId);
//	    } else {
////	        reviewList = tripPlaceDAO.findAllWithUserOrderDate(contentId);
//	    }
//
//	    for (TripPlaceReviewDTO review : reviewList) {
	    	// 유저가 지금까지 작성한 리뷰 수 추가
//	        int userReviewCount = tripPlaceDAO.getUserReviewCount(review.getUserId().toString());
//	        review.setUserReviewCnt(userReviewCount);
//
//	        // 리뷰별 이미지 리스트 추가
//	        List<ReviewImage> reviewImgList = tripPlaceDAO.findImgListById(review.getReviewId());
//	        List<HashMap<String, String>> imgList = new ArrayList<>();
//
//	        for (ReviewImage img : reviewImgList) {
//	        	HashMap<String, String> imgDetails = new HashMap<>();
//	            imgDetails.put("imgPath", img.getImagePath());
//	            imgList.add(imgDetails);
//	        }
//	        review.setReviewImgList(imgList);
//	    }
//	    return reviewList;
//	}
}
