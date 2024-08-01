package com.msgs.tripstory.dao;

import com.msgs.msgs.entity.tripstory.StoryImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripStoryImgDAO extends JpaRepository<StoryImg, Integer> {
}
