package com.evandro.e_commerce.order.factory;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerStatus;
import com.evandro.e_commerce.order.exception.InvalidOrderDataException;
import com.evandro.e_commerce.order.model.Order;

public class OrderFactory {

    public static Order create(Customer customer) {
        validateInputs(customer);
        return new Order(customer);
    }

    private static void validateInputs(Customer customer) {
        if (customer == null) {
            throw new InvalidOrderDataException("Customer cannot be null when creating an order.");
        }
        if (customer.getRegisterInfo().getStatus() != CustomerStatus.ACTIVE) {
            throw new InvalidOrderDataException("Order cannot be created for an inactive customer.");
        }
    }
}
