package com.igloo.common.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public record BeerOrderEvent(
    Long consumptionId,
    Long userId,
    LocalDateTime timestamp
) implements Serializable {
}