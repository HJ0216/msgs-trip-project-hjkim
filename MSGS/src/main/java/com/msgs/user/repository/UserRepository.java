package com.msgs.user.repository;

import com.msgs.msgs.entity.user.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository{
    private final EntityManager em;

    public List<User> findByEmail(String email) {
        return em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
    }

    public void save(User user) {
        em.persist(user);
    }

    public User findOne(Integer id){
        return em.find(User.class, id);
    }
}
