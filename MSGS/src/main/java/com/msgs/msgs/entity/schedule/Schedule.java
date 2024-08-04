package com.msgs.msgs.entity.schedule;

import com.msgs.msgs.entity.destination.Destination;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@IdClass(ScheduleId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

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

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedDate;

}
