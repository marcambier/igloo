package com.igloo.bar.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "/inventory")
public interface InventoryServiceClient {

    @GetExchange
    Integer getStock();
}