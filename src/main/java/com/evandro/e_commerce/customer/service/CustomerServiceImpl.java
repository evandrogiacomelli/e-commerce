package com.evandro.e_commerce.customer.service;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(CustomerDocuments documents, CustomerAddress address, CustomerRegisterInfo registerInfo) {
        return null;
    }

    @Override
    public Optional<Customer> findCustomerById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Customer> listAllCustomer() {
        return List.of();
    }

    @Override
    public List<Customer> listActiveCustomer() {
        return List.of();
    }

    @Override
    public Customer updateCustomer(UUID id, CustomerDocuments documents, CustomerAddress address, CustomerRegisterInfo registerInfo) {
        return null;
    }

    @Override
    public Customer deactivateCustomer(UUID id) {
        return null;
    }

    @Override
    public Customer activateCustomer(UUID id) {
        return null;
    }
}
