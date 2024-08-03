package com.msgs.tripschedule.dao;

import com.msgs.msgs.entity.tripschedule.DayDestination;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailScheduleDAO extends JpaRepository<DayDestination, Integer> {
    List<DayDestination> findAllByTripDay_Id(int dailyId);


}
