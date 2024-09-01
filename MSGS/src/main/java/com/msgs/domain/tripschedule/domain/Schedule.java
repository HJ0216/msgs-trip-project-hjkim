package com.msgs.domain.tripschedule.domain;

import com.msgs.global.common.model.BaseEntity;
import com.msgs.domain.tripplace.domain.Destination;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@IdClass(ScheduleId.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "schedule_id")
    private Integer id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="trip_id", insertable = false, updatable = false)
    private Trip trip;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="destination_id", insertable = false, updatable = false)
    private Destination destination;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int sequence;

    private String memo;
}