package com.igloo.inventory.controller;

import com.igloo.inventory.exception.InventoryServiceException;
import com.igloo.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Integer> getStock() {
        int stock = inventoryService.getStock();
        // FIXME : return a DTO as a json response instead of an integer
        return ResponseEntity.ok(stock);
    }

    @PostMapping("/refill")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> refillStock(@RequestParam int quantity) throws InventoryServiceException {
        int newStock = inventoryService.refillStock(quantity);
        // FIXME : return a DTO as a json response instead of an integer
        return ResponseEntity.ok(newStock);
    }
}