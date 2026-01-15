package com.igloo.inventory.listener;

import com.igloo.common.config.RabbitMQConfig;
import com.igloo.common.event.BeerOrderEvent;
import com.igloo.inventory.exception.InventoryServiceException;
import com.igloo.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BeerOrderEventListener {

    private final InventoryService inventoryService;

    @RabbitListener(queues = RabbitMQConfig.BEER_ORDER_QUEUE)
    public void handleBeerOrdered(BeerOrderEvent event) throws InventoryServiceException {
        log.info("Received BeerOrderedEvent: consumptionId={}, userId={}",
            event.consumptionId(), event.userId());

        try {
            inventoryService.decreaseStock();
        } catch (Exception e) {
            log.error("Error processing BeerOrderedEvent: {}", event, e);
            // TODO : for now, rethrow Exception as RabbitMQ will handle retry
            //   we could handle a DLQ
            throw e;
        }
    }
}