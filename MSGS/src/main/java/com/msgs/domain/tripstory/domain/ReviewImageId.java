package com.msgs.domain.tripstory.domain;

import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImageId implements Serializable {

  private Integer id;
  private Review review;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReviewImageId that = (ReviewImageId) o;
    return Objects.equals(getId(), that.getId()) && Objects.equals(getReview(), that.getReview());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getReview());
  }
}
