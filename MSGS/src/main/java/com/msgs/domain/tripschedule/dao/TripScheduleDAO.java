package com.msgs.domain.tripschedule.dao;

import com.msgs.domain.tripschedule.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripScheduleDAO extends JpaRepository<Trip, Integer> {

}

