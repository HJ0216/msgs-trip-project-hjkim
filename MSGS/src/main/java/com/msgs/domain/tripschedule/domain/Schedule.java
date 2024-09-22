package com.msgs.domain.tripschedule.domain;

import com.msgs.domain.tripstory.domain.Destination;
import com.msgs.global.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(ScheduleId.class)
@Getter
@Setter
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
  @JoinColumn(name = "trip_id", insertable = false, updatable = false)
  private Trip trip;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "destination_id", insertable = false, updatable = false)
  private Destination destination;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private int sequence;

  private String memo;
}