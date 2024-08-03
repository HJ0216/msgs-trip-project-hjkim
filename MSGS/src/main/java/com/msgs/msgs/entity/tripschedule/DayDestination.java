package com.msgs.msgs.entity.tripschedule;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayDestination {

    @Id @GeneratedValue
    @Column(name="destination_id")
    private Integer id;

    //join with trip schedule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    private TripDay tripDay;

    @Column(name = "orders", nullable = false)
    private int order;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 15, nullable = false)
    private String type;

    @Column(length = 255)
    private String address;

    @Column(columnDefinition = "decimal(10, 6)")
    private Double longitude;

    @Column(columnDefinition = "decimal(10, 6)")
    private Double latitude;

    @Column(length = 255)
    private String memo;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedDate;
}
