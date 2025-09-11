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
        this.inactiveIn = null;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public LocalDateTime getInactiveIn() {
        return inactiveIn;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public void setInactiveIn(LocalDateTime inactiveIn) {
        this.inactiveIn = inactiveIn;
    }
}
