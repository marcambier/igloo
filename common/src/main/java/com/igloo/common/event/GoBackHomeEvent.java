package com.igloo.common.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public record GoBackHomeEvent(
    Long userId,
    String message,
    LocalDateTime timestamp
) implements Serializable {
}
