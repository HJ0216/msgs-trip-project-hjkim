package com.msgs.msgs.dto;

import com.msgs.msgs.entity.tripschedule.TripDetailSchedule;
import com.msgs.msgs.entity.tripschedule.TripSchedule;
import com.msgs.msgs.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private int scheduleId;
    private String cityName; // 도시이름
    private String dateList; // 여행 일자 리스트
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    //
    private int placeCnt;

    public MyPageScheduleDTO(User user) {
        this.userId = user.getId();
    }
    public MyPageScheduleDTO(TripSchedule tripSchedule) {
        this.scheduleId = tripSchedule.getId();
        this.cityName = tripSchedule.getCityName();
        this.dateList = tripSchedule.getDateList();
        this.regDate = tripSchedule.getRegDate();
        this.modDate = tripSchedule.getModDate();
    }

    public MyPageScheduleDTO(User user, TripSchedule tripSchedule){
        this.userId = user.getId();
        this.scheduleId = tripSchedule.getId();
        this.cityName = tripSchedule.getCityName();
        this.dateList = tripSchedule.getDateList();
        this.regDate = tripSchedule.getRegDate();
        this.modDate = tripSchedule.getModDate();
    }

    public MyPageScheduleDTO(User user, TripSchedule tripSchedule, TripDetailSchedule tripDetailSchedule){
        this.userId = user.getId();
        this.scheduleId = tripSchedule.getId();
        this.cityName = tripSchedule.getCityName();
        this.dateList = tripSchedule.getDateList();
        this.regDate = tripSchedule.getRegDate();
        this.modDate = tripSchedule.getModDate();
    }


}
