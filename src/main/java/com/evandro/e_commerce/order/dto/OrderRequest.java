package com.evandro.e_commerce.order.dto;

import java.util.UUID;

public class OrderRequest {

    private UUID customerId;

    public OrderRequest() {
    } 

    public OrderRequest(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
}
