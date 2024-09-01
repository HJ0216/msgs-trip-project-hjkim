package com.msgs.domain.tripschedule.domain;

import com.msgs.global.common.model.BaseEntity;
import com.msgs.domain.user.domain.User;
import jakarta.persistence.*;

import java.time.LocalDate;

import lombok.*;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "trip_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(length = 100)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}