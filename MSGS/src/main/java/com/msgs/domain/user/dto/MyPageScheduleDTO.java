package com.msgs.domain.user.dto;

import com.msgs.domain.tripschedule.domain.Trip;
import com.msgs.domain.user.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageScheduleDTO {

  // Entity
  // User
  private Integer userId;

  // trip schedule
  private Integer tripId;
  private String title;
  private String city;
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;

  private int placeCnt;

  public MyPageScheduleDTO(User user) {
    this.userId = user.getId();
  }

  public MyPageScheduleDTO(Trip trip) {
    this.tripId = trip.getId();
    this.title = trip.getTitle();
    this.startDate = trip.getStartDate();
    this.endDate = trip.getEndDate();
    this.createdDate = trip.getCreatedDate();
    this.updatedDate = trip.getUpdatedDate();
  }

  public MyPageScheduleDTO(User user, Trip trip) {
    this.userId = user.getId();
    this.tripId = trip.getId();
    this.title = trip.getTitle();
    this.startDate = trip.getStartDate();
    this.endDate = trip.getEndDate();
    this.createdDate = trip.getCreatedDate();
    this.updatedDate = trip.getUpdatedDate();
  }
}
