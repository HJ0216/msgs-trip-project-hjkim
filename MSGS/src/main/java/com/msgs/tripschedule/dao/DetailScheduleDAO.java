package com.msgs.tripschedule.dao;

import com.msgs.msgs.entity.destination.Destination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailScheduleDAO extends JpaRepository<Destination, Integer> {
//    List<Destination> findAllByTripDay_Id(int dailyId);


}
