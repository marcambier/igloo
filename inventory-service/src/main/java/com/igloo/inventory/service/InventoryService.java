package com.igloo.inventory.service;

import static com.igloo.inventory.exception.InventoryServiceException.UNKNOWN_INVENTORY;
import static java.time.LocalDateTime.now;

import com.igloo.common.config.RabbitMQConfig;
import com.igloo.common.event.LowStockEvent;
import com.igloo.inventory.bo.InventoryBo;
import com.igloo.inventory.entity.InventoryEntity;
import com.igloo.inventory.exception.InventoryServiceException;
import com.igloo.inventory.repository.InventoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${inventory.low-stock-threshold}")
    private int lowStockThreshold;

    // FIXME : database initialisation for test purpose only
    @PostConstruct
    public void init() {
        if (repository.count() == 0) {
            InventoryEntity inventory = new InventoryEntity();
            inventory.setStockQuantity(10);
            inventory.setLastUpdated(now());
            repository.save(inventory);
            log.info("Initialized inventory with 10 beers");
        }
    }

    public int getStock() {
        return repository.findFirstByOrderByLastUpdatedDesc()
            .map(InventoryEntity::getStockQuantity)
            .orElse(0);
    }

    @Transactional
    public int refillStock(int quantity) throws InventoryServiceException {
        InventoryEntity inventoryEntity = getLastInventory();

        // FIXME : handle Entities<>BusinessObjects<>Dtos mapping in a proper way
        InventoryBo inventoryBo = new InventoryBo(inventoryEntity.getId(), inventoryEntity.getStockQuantity(), inventoryEntity.getLastUpdated());

        inventoryBo.increaseStock(quantity);

        updateInventory(inventoryEntity, inventoryBo);

        log.info("Stock refilled by {}. New stock: {}", quantity, inventoryEntity.getStockQuantity());

        return inventoryEntity.getStockQuantity();
    }

    @Transactional
    public void decreaseStock() throws InventoryServiceException {
        InventoryEntity inventoryEntity = getLastInventory();

        // FIXME : handle Entities<>BusinessObjects<>Dtos mapping in a proper way
        InventoryBo inventoryBo = new InventoryBo(inventoryEntity.getId(), inventoryEntity.getStockQuantity(), inventoryEntity.getLastUpdated());
        int currentStock = inventoryBo.getStockQuantity();

        inventoryBo.decreaseStock();

        int newStock = inventoryBo.getStockQuantity();
        log.info("Stock decreased. Previous: {}, New: {}", currentStock, inventoryBo.getStockQuantity());

        updateInventory(inventoryEntity, inventoryBo);

        if (newStock < lowStockThreshold) {
            sendLowStockEvent(inventoryBo.getStockQuantity());
        }
    }

    private void updateInventory(InventoryEntity inventoryEntity, InventoryBo inventoryBo) {
        inventoryEntity.setStockQuantity(inventoryBo.getStockQuantity());
        inventoryEntity.setLastUpdated(inventoryBo.getLastUpdated());
        repository.save(inventoryEntity);
    }

    private InventoryEntity getLastInventory() throws InventoryServiceException {
        return repository.findFirstByOrderByLastUpdatedDesc()
            .orElseThrow(() -> new InventoryServiceException(UNKNOWN_INVENTORY, "Inventory not found"));
    }

    private void sendLowStockEvent(int currentStock) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.STOCK_LOW_ROUTING_KEY,
            new LowStockEvent(currentStock, lowStockThreshold, now())
        );
        log.warn("Low stock event sent. Current stock: {}", currentStock);
    }
}