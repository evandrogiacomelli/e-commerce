package com.evandro.e_commerce.order.factory;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.order.model.Order;
import com.evandro.e_commerce.product.exception.InvalidOrderDataException;

public class OrderFactory {

    public static Order create(Customer customer) {
        validateInputs(customer);
        return new Order(customer);
    }

    private static void validateInputs(Customer customer) {
        if (customer == null) {
            throw new InvalidOrderDataException("Customer cannot be null when creating an order.");
        }
        // Poderíamos adicionar mais validações aqui para o cliente, se necessário,
        // como verificar se o cliente está ativo, etc.
    }
}
