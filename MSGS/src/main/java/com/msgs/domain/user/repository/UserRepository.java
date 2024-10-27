package com.msgs.domain.user.repository;

import com.msgs.domain.user.domain.User;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final EntityManager em;

  public Optional<User> findByEmail(String email) {
    List<User> users = em.createQuery("select u from User u where u.email = :email", User.class)
                         .setParameter("email", email)
                         .getResultList();
    
    return users.stream().findFirst();
  }

  public void save(User user) {
    em.persist(user);
  }

  public Optional<User> findById(Integer id) {
    User user = em.find(User.class, id);
    return Optional.ofNullable(user);
  }
}
