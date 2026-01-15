package com.igloo.bar.dto;

import java.time.LocalDateTime;

public record BeerConsumptionDto(
    Long id,
    Long userId,
    LocalDateTime consumedAt
) {
}