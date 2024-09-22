package com.msgs.domain.user.dto;

import com.msgs.domain.tripstory.domain.Review;
import com.msgs.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageReviewDTO {

  // Entity
  // User
  private int userId;

  // Like
  private String likeId;

  // place review
  private Integer reviewId;
  private String comments; // 내용
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;

  public MyPageReviewDTO(User user, Review review) {
    this.userId = user.getId();
    this.reviewId = review.getId();
    this.comments = review.getComments();
    this.createdDate = review.getCreatedDate();
    this.updatedDate = review.getUpdatedDate();
  }


}
