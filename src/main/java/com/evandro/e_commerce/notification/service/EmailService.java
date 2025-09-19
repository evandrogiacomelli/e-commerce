package com.evandro.e_commerce.notification.service;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.order.model.Order;

public interface EmailService {
    void sendOrderUpdateEmail(Customer customer, Order order);
}