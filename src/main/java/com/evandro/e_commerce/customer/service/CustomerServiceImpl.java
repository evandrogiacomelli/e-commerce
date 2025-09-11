package com.evandro.e_commerce.customer.service;

import com.evandro.e_commerce.customer.exception.CustomerNotFoundException;
import com.evandro.e_commerce.customer.factory.CustomerFactory;
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
        Customer customer = CustomerFactory.create(documents, address, registerInfo);
        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> findCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    @Override
    public List<Customer> listAllCustomer() {
        return customerRepository.findAll();
    }

    @Override
    public List<Customer> listActiveCustomer() {
        return customerRepository.findActiveCustomers();
    }

    @Override
    public Customer updateCustomer(UUID id, CustomerDocuments documents, CustomerAddress address, CustomerRegisterInfo registerInfo) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found."));

        CustomerFactory.create(documents, address, registerInfo);

        customer.update(documents, address, registerInfo);
        return customerRepository.save(customer);
    }

    @Override
    public Customer deactivateCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found."));
        customer.deactivate();
        return customerRepository.save(customer);
    }

    @Override
    public Customer activateCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found."));
        customer.activate();
        return customerRepository.save(customer);
    }
}
