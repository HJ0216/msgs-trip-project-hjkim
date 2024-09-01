package com.msgs.domain.user.repository;

import com.msgs.domain.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository{
    private final EntityManager em;

    public Optional<User> findByEmail(String email) {
        List<User> users = em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
        if (users.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(users.get(0));
        }
    }

    public void save(User user) {
        em.persist(user);
    }

    public Optional<User> findById(Integer id){
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }
}
