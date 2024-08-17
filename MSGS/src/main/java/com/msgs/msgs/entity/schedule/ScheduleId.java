package com.msgs.msgs.entity.schedule;

import com.msgs.msgs.entity.destination.Destination;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ScheduleId implements Serializable {

    private Integer id;
    private Trip trip;
    private Destination destination;
}
