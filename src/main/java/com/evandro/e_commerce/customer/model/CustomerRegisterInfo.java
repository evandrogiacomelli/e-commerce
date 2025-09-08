package com.evandro.e_commerce.customer.model;

import java.time.LocalDateTime;

public class CustomerRegisterInfo {
    private LocalDateTime registerDate;
    private LocalDateTime lastAccess;
    private CustomerStatus status;
    private LocalDateTime inactiveIn;
}
