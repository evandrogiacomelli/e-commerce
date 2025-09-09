package com.evandro.e_commerce.customer.model;

import java.time.LocalDateTime;

public class CustomerRegisterInfo {
    private LocalDateTime registerDate;
    private LocalDateTime lastAccess;
    private CustomerStatus status;
    private LocalDateTime inactiveIn;

    public CustomerRegisterInfo(CustomerStatus status) {
        this.registerDate = LocalDateTime.now();
        this.lastAccess = LocalDateTime.now();
        this.status = status;
        this.inactiveIn = inactiveIn;
    }
}
