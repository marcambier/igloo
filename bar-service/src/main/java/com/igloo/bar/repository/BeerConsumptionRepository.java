package com.igloo.bar.repository;

import com.igloo.bar.entity.BeerConsumptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeerConsumptionRepository extends JpaRepository<BeerConsumptionEntity, Long> {
}