package com.msgs.domain.tripstory.domain;

import com.msgs.global.common.model.BaseEntity;
import com.msgs.domain.tripschedule.domain.Schedule;
import com.msgs.domain.tripschedule.domain.Trip;
import jakarta.persistence.*;
import lombok.*;

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