package com.skillbox.cryptobot.configuration;

import com.skillbox.cryptobot.service.ScheduledService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;

@EnableScheduling
@RequiredArgsConstructor
@Configuration
public class ScheduledConfig implements SchedulingConfigurer {

    private final ScheduledService scheduledService;

    @Value("${telegram.bot.notify.delay.value}")
    private long notifyDelay;

    @Value("${telegram.bot.notify.delay.unit}")
    private String notifyDelayUnit;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(5));

        ChronoUnit unit = switch (notifyDelayUnit) {
            case "MILLIS" -> ChronoUnit.MILLIS;
            case "SECONDS" -> ChronoUnit.SECONDS;
            case "HOURS" -> ChronoUnit.HOURS;
            case "DAYS" -> ChronoUnit.DAYS;
            default -> ChronoUnit.MINUTES;
        };

        taskRegistrar.addFixedDelayTask(scheduledService::chekBitcoinPrice, Duration.of(2, ChronoUnit.MINUTES));
        taskRegistrar.addFixedDelayTask(scheduledService::notifySubscribers, Duration.of(notifyDelay, unit));
    }
}
