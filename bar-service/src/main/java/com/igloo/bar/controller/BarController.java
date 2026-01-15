package com.igloo.bar.controller;

import com.igloo.bar.dto.BeerConsumptionDto;
import com.igloo.bar.entity.BeerConsumptionEntity;
import com.igloo.bar.exception.BarServiceException;
import com.igloo.bar.service.BarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bar")
@RequiredArgsConstructor
public class BarController {

    private final BarService barService;

    @PostMapping("/consumption")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BeerConsumptionDto> createConsumption(@RequestParam Long userId) throws BarServiceException {
        BeerConsumptionEntity consumptionEntity = barService.createConsumption(userId);
        // FIXME : improve all Entity<>Dto mappings (using MapStruct ?)
        BeerConsumptionDto consumptionDto = new BeerConsumptionDto(consumptionEntity.getId(), consumptionEntity.getUserId(), consumptionEntity.getConsumedAt());
        return ResponseEntity.ok(consumptionDto);
    }
}