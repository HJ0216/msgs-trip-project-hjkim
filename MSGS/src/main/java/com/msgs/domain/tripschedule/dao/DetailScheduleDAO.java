package com.msgs.domain.tripschedule.dao;

import com.msgs.domain.tripplace.domain.Destination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailScheduleDAO extends JpaRepository<Destination, Integer> {
//    List<Destination> findAllByTripDay_Id(int dailyId);


}
