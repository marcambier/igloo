package com.igloo.gobackhome.service;

import static java.time.LocalDateTime.now;

import com.igloo.common.config.RabbitMQConfig;
import com.igloo.common.event.BeerOrderEvent;
import com.igloo.common.event.GoBackHomeEvent;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoBackHomeService {

    /*
    TODO : Rework this service architecture to make it safe and scalable :
      - listener app consumes the queue and persist them in dedicated database
      - batch app is triggered by an external orchestrator to send events at 2AM

      An alternative would be to use a rabbit "waiting queue" pattern to delay messages until 2 AM.
      That would avoid having a dedicated batch.
     */

    private final RabbitTemplate rabbitTemplate;

    // FIXME: in-memory persistence is not a safe and scalable way to handle this
    //  TODO: persist in a dedicated database
    private final Set<Long> customersToSendBackHome = ConcurrentHashMap.newKeySet(128);

    @RabbitListener(queues = RabbitMQConfig.BEER_ORDER_TIMEOUT_QUEUE)
    @Transactional
    public void handleBeerOrdered(BeerOrderEvent event) {
        log.info("Received BeerOrderedEvent: {}", event);
        if (event.timestamp().getHour() >= 20) {
            this.customersToSendBackHome.add(event.userId());
        }
    }

    // FIXME: wrong way to launch a batch, and that's not scalable
    //  TODO: create a dedicated batch app, and use an external orchestrator to launch it (eg. Kubernetes CronJob)
    @Scheduled(cron = "0 0 2 * * *")
    public void sendGoBackHomeNotifications() {
        log.info("Running Go Back Home notification job at 2 AM");
        customersToSendBackHome.forEach(this::sendGoBackHomeNotification);
        customersToSendBackHome.clear();
    }

    private void sendGoBackHomeNotification(Long userId) {
        log.info("Sending 'Go Back Home' notification for user: {}", userId);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.GO_BACK_HOME_ROUTING_KEY,
            new GoBackHomeEvent(userId, "Go back home baby", now())
        );
    }
}