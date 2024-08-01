package com.msgs.msgs.entity.tripschedule;

import com.msgs.msgs.entity.tripstory.TripStory;
import com.msgs.msgs.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id @GeneratedValue
    @Column(name = "trip_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(length = 100)
    private String title;

    @Column(length = 30)
    private String city;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedDate;

    //mapping
    @OneToOne(mappedBy = "trip", fetch = FetchType.LAZY)
    private TripStory tripStory;

    @OneToMany(mappedBy = "trip", fetch = FetchType.LAZY)
    private  List<TripDailySchedule> tripDailySchedules = new ArrayList<>();
}


