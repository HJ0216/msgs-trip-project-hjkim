package com.msgs.msgs.dto;

import com.msgs.msgs.entity.tripschedule.TripDetailSchedule;
import com.msgs.msgs.entity.tripschedule.Trip;
import com.msgs.msgs.entity.user.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        this.city = trip.getCity();
        this.startDate = trip.getStartDate();
        this.endDate = trip.getEndDate();
        this.createdDate = trip.getCreatedDate();
        this.updatedDate = trip.getUpdatedDate();
    }

    public MyPageScheduleDTO(User user, Trip trip){
        this.userId = user.getId();
        this.tripId = trip.getId();
        this.title = trip.getTitle();
        this.city = trip.getCity();
        this.startDate = trip.getStartDate();
        this.endDate = trip.getEndDate();
        this.createdDate = trip.getCreatedDate();
        this.updatedDate = trip.getUpdatedDate();
    }
}
