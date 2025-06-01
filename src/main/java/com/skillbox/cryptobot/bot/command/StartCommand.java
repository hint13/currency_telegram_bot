package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.SubscriberService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * Обработка команды начала работы с ботом
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StartCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Запускает бота";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = createAnswer(message.getChatId());
        try {
            absSender.execute(answer);
            long userId = message.getFrom().getId();
            if (subscriberService.getSubscriber(userId) == null) {
                subscriberService.createSubscriber(userId);
                log.info("New subscriber with id {} created.", userId);
            } else {
                log.info("Subscriber with id {} used bot.", userId);
            }
        } catch (TelegramApiException e) {
            log.error("Error occurred in /start command", e);
        }
    }

    private static SendMessage createAnswer(Long chatId) {
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);

        answer.setText("""
                Привет! Данный бот помогает отслеживать стоимость биткоина.
                Поддерживаемые команды:
                 /subscribe [число] - подписаться на стоимость биткоина в USD
                 /get_price - получить стоимость биткоина
                 /get_subscription - получить текущую подписку
                 /unsubscribe - отменить подписку на стоимость
                """);
        return answer;
    }
}