package com.msgs.msgs.dto;

import com.msgs.msgs.entity.tripschedule.TripDailySchedule;
import com.msgs.msgs.entity.tripschedule.TripDetailSchedule;
import com.msgs.msgs.entity.tripschedule.Trip;
import com.msgs.msgs.entity.user.User;

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
public class ScheduleEntityDTO {

    //Trip Entity
    private Integer id;
    private User user;
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    //TripDailySchedule Entity
    private int dailyId;
    private Trip trip; //int id

    //TripDetailSchedule Entity
    private int orderDay;
    private TripDailySchedule tripDailySchedule; //int daily_
    private int order;
    private int placeOrder;
    private String title;
    private String type;
    private String location;
    private Double mapx;
    private Double mapy;
    private String contentid;

    public ScheduleEntityDTO(Trip trip) {
        this.id = trip.getId();
        this.user = trip.getUser();
        this.city = trip.getCity();
        this.startDate = trip.getStartDate();
        this.endDate = trip.getEndDate();
        this.createdDate = trip.getCreatedDate();
        this.updatedDate = trip.getUpdatedDate();
    }

    public ScheduleEntityDTO(TripDailySchedule tripDailySchedule) {
        this.dailyId = tripDailySchedule.getDailyId();
        this.trip = tripDailySchedule.getTrip();
    }

    public ScheduleEntityDTO(TripDetailSchedule tripDetailSchedule) {
        this.orderDay = tripDetailSchedule.getOrderDay();
        this.tripDailySchedule = tripDetailSchedule.getTripDailySchedule();
        this.order = tripDetailSchedule.getOrder();
        this.placeOrder = tripDetailSchedule.getPlaceOrder();
        this.title = tripDetailSchedule.getTitle();
        this.type = tripDetailSchedule.getType();
        this.location = tripDetailSchedule.getLocation();
        this.mapx = tripDetailSchedule.getMapx();
        this.mapy = tripDetailSchedule.getMapy();
        this.contentid = tripDetailSchedule.getContentid();
    }
}
