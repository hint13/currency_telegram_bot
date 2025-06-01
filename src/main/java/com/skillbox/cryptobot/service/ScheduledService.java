package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.bot.CryptoBot;
import com.skillbox.cryptobot.data.model.Subscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ScheduledService {

    private final SubscriberService subscriberService;
    private final CryptoCurrencyService cryptoCurrencyService;
    private final CryptoBot cryptoBot;

    private double bitcoinPrice = -1;

//    @Scheduled(cron = "0 */2 * * * *")
    public void chekBitcoinPrice() {
        try {
            double currentPrice = cryptoCurrencyService.getBitcoinPrice();
            bitcoinPrice = currentPrice;
            log.info("Current bitcoin price: {} USD", currentPrice);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

//    @Scheduled(cron = "10 */10 * * * *")
    public void notifySubscribers() {
        if (bitcoinPrice < 0)
            return;
        try {
            log.info("Check subscribers for notify");
            List<Subscriber> subscribers = subscriberService.getSubscribersForPrice(bitcoinPrice);
            if (subscribers != null && !subscribers.isEmpty()) {
                subscribers.forEach(subscriber -> {
                    SendMessage message = new SendMessage();
                    message.setAllowSendingWithoutReply(true);
                    message.setChatId(subscriber.getUser_id());
                    message.setText(MessageFormat.format("Пора покупать, стоимость биткоина {0} USD", bitcoinPrice));
                    try {
                        cryptoBot.execute(message);
                    } catch (TelegramApiException e) {
                        log.error(e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
