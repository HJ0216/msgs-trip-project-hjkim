package com.msgs.domain.tripschedule.domain;

import com.msgs.domain.tripstory.domain.Destination;
import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ScheduleId implements Serializable {

    private Integer id;
    private Trip trip;
    private Destination destination;
}
