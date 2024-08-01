package com.msgs.tripschedule.dao;

import com.msgs.msgs.entity.tripschedule.TripDailySchedule;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyScheduleDAO extends JpaRepository<TripDailySchedule, Integer>  {
//    List<TripDailySchedule> findAllByTripSchedule_Schedule_id(int id);
    List<TripDailySchedule> findAllByTrip_Id(int id);
}
