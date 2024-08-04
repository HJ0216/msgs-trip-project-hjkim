package com.msgs.msgs.entity.schedule;

import com.msgs.msgs.entity.destination.Destination;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleId implements Serializable {

    private Integer id;
    private Trip trip;
    private Destination destination;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleId that = (ScheduleId) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getTrip(), that.getTrip()) && Objects.equals(getDestination(), that.getDestination());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTrip(), getDestination());
    }
}
