package com.msgs.msgs.entity.tripschedule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
@NoArgsConstructor
public class TripDay {

    @Id @GeneratedValue
    @Column(name = "day_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column
    private int dayNumber;

    @Column
    private LocalDate dayDate;

    //mapping
    @OneToMany(mappedBy = "tripDay", fetch = FetchType.LAZY)
    private List<DayDestination> dayDestinations = new ArrayList<>();
}
