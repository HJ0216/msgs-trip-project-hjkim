package com.msgs.domain.tripstory.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripStoryMainDTO {

	// TripStory
    private int storyId;
    private int scheduleId;
    private String cityName;

    private String title;
    private String comment;
    private String dateList;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    
    
    // private UserEntity userEntity;
    private Integer userId;
    private String userName;
    
    // private UserImg userImg;
    private String userImgPath;
    
    // find
    private String storyImgOriginName;
    private String storyImgPath;

}
