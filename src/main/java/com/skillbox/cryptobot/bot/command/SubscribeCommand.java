package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.SubscriberService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.MessageFormat;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        var user = message.getFrom();
        if (user != null) {
            double price = 0;
            try {
                price = Double.parseDouble(arguments[0]);
            } catch (Exception ignored) {
            }
            String text;
            if (price > 0) {
                text = MessageFormat.format("{0} подписан на курс биткоина в {1} USD.",
                        String.join(" ", user.getFirstName(), user.getLastName()), TextUtil.toString(price));
                subscriberService.subscribe(user.getId(), price);
            } else {
                text = "Курс меньше или равен \"0\". Подписка не выполнена.";
            }
            log.info(text);
            answer.setText(text);
        } else
            answer.setText("echo: subscribe");

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /subscribe command", e);
        }
    }
}