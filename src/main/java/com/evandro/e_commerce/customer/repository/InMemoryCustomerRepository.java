package com.evandro.e_commerce.customer.repository;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.product.model.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<UUID, Customer> customers = new ConcurrentHashMap<>();

    @Override
    public Customer saveCustomer(Customer customer) {
        return null;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        return List.of();
    }

    @Override
    public List<Customer> findActiveCustomer() {
        return List.of();
    }

    @Override
    public Customer updateCustomer(UUID id, CustomerDocuments documents, CustomerAddress address, CustomerRegisterInfo info) {
        return null;
    }

    @Override
    public Customer deactiveCustomer(UUID id) {
        return null;
    }

    @Override
    public Customer reactivateCustomer(UUID id) {
        return null;
    }
}
