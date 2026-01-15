package com.igloo.bar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "beer_consumption")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeerConsumptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime consumedAt;

    public BeerConsumptionEntity(Long userId, LocalDateTime consumedAt) {
        this.userId = userId;
        this.consumedAt = consumedAt;
    }
}