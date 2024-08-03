package com.msgs.tripschedule.dao;

import com.msgs.msgs.entity.tripschedule.TripDay;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyScheduleDAO extends JpaRepository<TripDay, Integer>  {
//    List<TripDay> findAllByTripSchedule_Schedule_id(int id);
    List<TripDay> findAllByTrip_Id(int id);
}
