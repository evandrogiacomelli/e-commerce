package com.evandro.e_commerce.customer.repository;

import com.evandro.e_commerce.customer.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    List<Customer> findAll();
    List<Customer> findActiveCustomers();
}
