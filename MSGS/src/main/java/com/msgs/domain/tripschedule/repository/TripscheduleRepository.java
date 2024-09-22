package com.msgs.domain.tripschedule.repository;


import com.msgs.domain.tripschedule.domain.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
//@RequiredArgsConstructor
@Transactional(readOnly = false)
public class TripscheduleRepository {

  @PersistenceContext
  private EntityManager em;

  public void saveTripSchedule(Trip trip) {
    em.persist(trip);
  }


}
