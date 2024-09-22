package com.msgs.domain.tripstory.repository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TripStoryRepository {

  private final EntityManager em;

}
