package com.evandro.e_commerce.customer.service;

import com.evandro.e_commerce.customer.dto.CustomerCreationRequest;
import com.evandro.e_commerce.customer.dto.CustomerDtoConverter;
import com.evandro.e_commerce.customer.dto.CustomerRequest;
import com.evandro.e_commerce.customer.dto.CustomerResponse;
import com.evandro.e_commerce.customer.exception.CustomerNotFoundException;
import com.evandro.e_commerce.customer.factory.CustomerFactory;
import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.repository.CustomerRepository;
import com.evandro.e_commerce.customer.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;
    private final CustomerFactory customerFactory;

    private final Validator<CustomerDocuments> documentsValidator;
    private final Validator<CustomerAddress> addressValidator;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               CustomerFactory customerFactory,
                               Validator<CustomerDocuments> documentsValidator,
                               Validator<CustomerAddress> addressValidator) {
        this.customerRepository = customerRepository;
        this.customerFactory = customerFactory;
        this.documentsValidator = documentsValidator;
        this.addressValidator = addressValidator;
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        var creationRequest = new CustomerCreationRequest(
                CustomerDtoConverter.toCustomerDocuments(request),
                CustomerDtoConverter.toCustomerAddress(request),
                CustomerDtoConverter.toCustomerRegisterInfo()
        );
        Customer customer = customerFactory.create(creationRequest);
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
        CustomerDocuments newDocuments = CustomerDtoConverter.toCustomerDocuments(request);
        CustomerAddress newAddress = CustomerDtoConverter.toCustomerAddress(request);
        CustomerRegisterInfo newRegisterInfo = CustomerDtoConverter.toCustomerRegisterInfo();
        documentsValidator.validate(newDocuments);
        addressValidator.validate(newAddress);
        customer.update(newDocuments, newAddress, newRegisterInfo);
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
