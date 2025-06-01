package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.data.model.Subscriber;
import com.skillbox.cryptobot.data.repository.SubscribersDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubscriberService {

    private final SubscribersDao subscribersDao;

    @Autowired
    public SubscriberService(SubscribersDao subscribersDao) {
        this.subscribersDao = subscribersDao;
    }

    public void createSubscriber(Long id) {
        Subscriber subscriber = Subscriber.builder()
                .user_id(id)
                .build();
        subscribersDao.save(subscriber);
    }

    public Subscriber getSubscriber(Long userId) {
        return subscribersDao.findByUserId(userId);
    }

    public List<Subscriber> getSubscribersForPrice(double price) {
        return subscribersDao.findAllWithPriceOrGreat(price);
    }

    public void subscribe(Long userId, double price) {
        Subscriber subscriber = getSubscriber(userId);
        if (subscriber == null) {
            subscriber = Subscriber.builder()
                    .user_id(userId)
                    .build();
        }
        subscriber.setSubscribe_price(price > 0 ? price : null);
        subscribersDao.save(subscriber);
    }

    public void unsubscribe(Long userId) {
        Subscriber subscriber = getSubscriber(userId);
        if (subscriber == null) {
            subscriber = Subscriber.builder()
                    .user_id(userId)
                    .build();
        }
        subscriber.setSubscribe_price(null);
        subscribersDao.save(subscriber);
    }
}
