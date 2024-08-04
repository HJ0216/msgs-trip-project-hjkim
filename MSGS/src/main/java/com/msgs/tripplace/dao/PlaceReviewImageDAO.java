package com.msgs.tripplace.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.msgs.msgs.entity.review.ReviewImage;

public interface PlaceReviewImageDAO extends JpaRepository<ReviewImage, String>{

}
