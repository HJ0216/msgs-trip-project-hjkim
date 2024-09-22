package com.msgs.domain.tripschedule.domain;

import com.msgs.domain.tripstory.domain.Destination;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ScheduleId implements Serializable {

  private Integer id;
  private Trip trip;
  private Destination destination;
}
