package com.evandro.e_commerce.customer.service;

import com.evandro.e_commerce.customer.dto.CustomerDtoConverter;
import com.evandro.e_commerce.customer.dto.CustomerRequest;
import com.evandro.e_commerce.customer.dto.CustomerResponse;
import com.evandro.e_commerce.customer.exception.CustomerNotFoundException;
import com.evandro.e_commerce.customer.factory.CustomerFactory;
import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = CustomerFactory.create(
                CustomerDtoConverter.toCustomerDocuments(request),
                CustomerDtoConverter.toCustomerAddress(request),
                CustomerDtoConverter.toCustomerRegisterInfo());
        Customer savedCustomer = customerRepository.save(customer);
        return new CustomerResponse(savedCustomer);
    }

    @Override
    public Optional<CustomerResponse> findCustomerById(UUID id) {
        return customerRepository.findById(id)
                .map(CustomerResponse::new);
    }

    @Override
    public List<CustomerResponse> listAllCustomer() {
        return customerRepository.findAll().stream()
                .map(CustomerResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerResponse> listActiveCustomer() {
        return customerRepository.findActiveCustomers().stream()
                .map(CustomerResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found."));

        CustomerFactory.create(
                CustomerDtoConverter.toCustomerDocuments(request),
                CustomerDtoConverter.toCustomerAddress(request),
                CustomerDtoConverter.toCustomerRegisterInfo());

        customer.update(
                CustomerDtoConverter.toCustomerDocuments(request),
                CustomerDtoConverter.toCustomerAddress(request),
                CustomerDtoConverter.toCustomerRegisterInfo());
        Customer savedCustomer = customerRepository.save(customer);
        return new CustomerResponse(savedCustomer);
    }

    @Override
    public CustomerResponse deactivateCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found."));
        customer.deactivate();
        Customer savedCustomer = customerRepository.save(customer);
        return new CustomerResponse(savedCustomer);
    }

    @Override
    public CustomerResponse activateCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + id + " not found."));
        customer.activate();
        Customer savedCustomer = customerRepository.save(customer);
        return new CustomerResponse(savedCustomer);
    }
}
