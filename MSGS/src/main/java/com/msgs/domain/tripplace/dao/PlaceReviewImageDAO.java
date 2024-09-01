package com.msgs.domain.tripplace.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.msgs.domain.tripplace.domain.ReviewImage;

public interface PlaceReviewImageDAO extends JpaRepository<ReviewImage, String>{

}
