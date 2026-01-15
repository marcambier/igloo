package com.igloo.inventory.bo;


import static com.igloo.inventory.exception.InventoryServiceException.INSUFFICIENT_STOCK;

import com.igloo.inventory.exception.InventoryServiceException;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InventoryBo {
    private Long id;
    private int stockQuantity;
    private LocalDateTime lastUpdated;

    public void decreaseStock() throws InventoryServiceException {
        if (this.stockQuantity <= 0) {
            throw new InventoryServiceException(INSUFFICIENT_STOCK, "Not enough beers");
        }
        this.stockQuantity -= 1;
        this.lastUpdated = LocalDateTime.now();
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
        this.lastUpdated = LocalDateTime.now();
    }
}
