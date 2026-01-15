package com.igloo.bar.service;

import static com.igloo.bar.exception.BarServiceException.INSUFFICIENT_STOCK;
import static java.time.LocalDateTime.now;

import com.igloo.bar.client.InventoryServiceClient;
import com.igloo.bar.entity.BeerConsumptionEntity;
import com.igloo.bar.exception.BarServiceException;
import com.igloo.bar.repository.BeerConsumptionRepository;
import com.igloo.common.config.RabbitMQConfig;
import com.igloo.common.event.BeerOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BarService {

    private final BeerConsumptionRepository repository;
    private final InventoryServiceClient inventoryClient;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public BeerConsumptionEntity createConsumption(Long userId) throws BarServiceException {

        checkStockAvailability();

        BeerConsumptionEntity consumption = createConsumptionEntity(userId);

        sendConsumptionEvent(consumption);

        log.info("Beer consumption created: {}", consumption.getId());
        return consumption;
    }

    private void checkStockAvailability() throws BarServiceException {
        if (inventoryClient.getStock().compareTo(0) <= 0) {
            throw new BarServiceException(INSUFFICIENT_STOCK, "No beer in stock");
        }
    }

    private BeerConsumptionEntity createConsumptionEntity(Long userId) {
        BeerConsumptionEntity consumption = new BeerConsumptionEntity(userId, now());
        consumption = repository.save(consumption);
        return consumption;
    }

    private void sendConsumptionEvent(BeerConsumptionEntity consumption) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE, RabbitMQConfig.BEER_ORDER_ROUTING_KEY,
            new BeerOrderEvent(
                consumption.getId(),
                consumption.getUserId(),
                consumption.getConsumedAt()
            )
        );
    }
}