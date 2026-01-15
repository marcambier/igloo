package com.igloo.common.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public record LowStockEvent(
    int currentStock,
    int threshold,
    LocalDateTime timestamp
) implements Serializable {
}
