package com.msgs.msgs.entity.review;

import com.msgs.msgs.entity.BaseEntity;
import com.msgs.msgs.entity.destination.Destination;
import com.msgs.msgs.entity.schedule.Schedule;
import com.msgs.msgs.entity.schedule.Trip;
import com.msgs.msgs.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", insertable = false, updatable = false)
    private Trip trip;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "schedule_id"),
            @JoinColumn(name = "trip_id"),
            @JoinColumn(name = "destination_id")
    })
    private Schedule schedule;

    private int rating;

    @Column(length = 1000)
    private String comments;
}