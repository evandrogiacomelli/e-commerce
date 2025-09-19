package com.evandro.e_commerce.customer.service;

import com.evandro.e_commerce.customer.dto.CustomerCreationRequest;
import com.evandro.e_commerce.customer.dto.CustomerDtoConverter;
import com.evandro.e_commerce.customer.dto.CustomerRequest;
import com.evandro.e_commerce.customer.dto.CustomerResponse;
import com.evandro.e_commerce.customer.exception.CustomerNotFoundException;
import com.evandro.e_commerce.customer.exception.DuplicateCpfException;
import com.evandro.e_commerce.customer.exception.DuplicateRgException;
import com.evandro.e_commerce.customer.exception.DuplicateEmailException;
import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.repository.CustomerRepository;
import com.evandro.e_commerce.customer.validation.Validator;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final Validator<CustomerDocuments> documentsValidator;
    private final Validator<CustomerAddress> addressValidator;

    public CustomerServiceImpl(CustomerRepository customerRepository,
            Validator<CustomerDocuments> documentsValidator,
            Validator<CustomerAddress> addressValidator) {
        this.customerRepository = customerRepository;
        this.documentsValidator = documentsValidator;
        this.addressValidator = addressValidator;
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerCreationRequest request) {
        documentsValidator.validate(request.documents());
        addressValidator.validate(request.address());
        validateUniqueDocuments(request.documents());
        Customer customer = new Customer(request.documents(), request.address(), request.registerInfo());
        return new CustomerResponse(customerRepository.save(customer));
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
        validateUniqueDocumentsForUpdate(newDocuments, id);
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

    private void validateUniqueDocuments(CustomerDocuments documents) {
        if (customerRepository.existsByCpf(documents.getCpf())) {
            throw new DuplicateCpfException("CPF " + documents.getCpf() + " is already registered");
        }
        if (customerRepository.existsByRg(documents.getRg())) {
            throw new DuplicateRgException("RG " + documents.getRg() + " is already registered");
        }
        if (documents.getEmail() != null && customerRepository.existsByEmail(documents.getEmail())) {
            throw new DuplicateEmailException("Email " + documents.getEmail() + " is already registered");
        }
    }

    private void validateUniqueDocumentsForUpdate(CustomerDocuments documents, UUID customerId) {
        if (customerRepository.existsByCpfExcludingId(documents.getCpf(), customerId)) {
            throw new DuplicateCpfException("CPF " + documents.getCpf() + " is already registered");
        }
        if (customerRepository.existsByRgExcludingId(documents.getRg(), customerId)) {
            throw new DuplicateRgException("RG " + documents.getRg() + " is already registered");
        }
        if (documents.getEmail() != null && customerRepository.existsByEmailExcludingId(documents.getEmail(), customerId)) {
            throw new DuplicateEmailException("Email " + documents.getEmail() + " is already registered");
        }
    }
}
