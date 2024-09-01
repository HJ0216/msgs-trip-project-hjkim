package com.msgs.domain.tripplace.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequestMapping("tripplace")
@RestController // JSON 또는 XML 형식의 데이터를 반환
@RequiredArgsConstructor // final variable에 대한 생성자 생성
public class TripPlaceController {
	
//	private final TripPlaceService tripPlaceService;
//
//    @PostMapping(value = "/reviewSubmit")
//    public void reviewSubmit(@RequestBody TripPlaceReviewDTO tripPlaceReviewDTO, HttpSession httpSess) throws Exception {
//    	System.out.println(tripPlaceReviewDTO.getBase64List());
//
//    	ImageUploadController imageUploadController = new ImageUploadController();
//
//    	List<HashMap<String, String>> reviewImgList =  imageUploadController.uploadFilesSample(
//	    									tripPlaceReviewDTO.getBase64List(),
//	    									"/review-image",
//	    									httpSess);
//
//    	System.out.println("업로드 컨트롤러 타고 나온 주소 리스트" + reviewImgList);
//
//    	tripPlaceReviewDTO.setReviewImgList(reviewImgList);
//
//    	tripPlaceService.reviewSubmit(tripPlaceReviewDTO);
//    }
//
//    @PostMapping(value = "/getReviewList")
//    public List<TripPlaceReviewDTO> getReviewList(@RequestBody String data) {
//    	JSONObject requestData = new JSONObject(data);
//    	Boolean isSortedByLike = requestData.getBoolean("isSortedByLike");
//    	String contentId = requestData.getString("contentId");
//
//    	return tripPlaceService.getReviewList(isSortedByLike, contentId);
//    }
}
