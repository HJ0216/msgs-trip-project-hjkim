package com.msgs.user.repository;

import com.msgs.msgs.entity.user.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository{
    private final EntityManager em;

    public void save(User user) {
        em.persist(user);
    }

    public User findOne(Integer id){
        return em.find(User.class, id);
    }
}
