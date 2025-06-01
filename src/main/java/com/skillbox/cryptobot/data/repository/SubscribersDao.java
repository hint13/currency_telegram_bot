package com.skillbox.cryptobot.data.repository;

import com.skillbox.cryptobot.data.model.Subscriber;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class SubscribersDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public SubscribersDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void save(Subscriber subscriber) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(subscriber);
    }

    @Transactional(readOnly = true)
    public Subscriber findByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        var query = session.createSelectionQuery("from subscribers where user_id = :userId", Subscriber.class);
        query.setParameter("userId", userId);
        try {
            return query.getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Subscriber> findAllWithPriceOrGreat(double price) {
        Session session = sessionFactory.getCurrentSession();
        var query = session.createSelectionQuery("from subscribers where subscribe_price >= :price", Subscriber.class);
        query.setParameter("price", price);
        try {
            return query.getResultList();
        } catch (PersistenceException e) {
            return null;
        }
    }
}
