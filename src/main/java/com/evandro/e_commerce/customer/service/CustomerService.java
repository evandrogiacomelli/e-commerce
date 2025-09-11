package com.evandro.e_commerce.customer.service;

import com.evandro.e_commerce.customer.dto.CustomerRequest;
import com.evandro.e_commerce.customer.dto.CustomerResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    Optional<CustomerResponse> findCustomerById(UUID id);
    List<CustomerResponse> listAllCustomer();
    List<CustomerResponse> listActiveCustomer();
    CustomerResponse updateCustomer(UUID id, CustomerRequest request);
    CustomerResponse deactivateCustomer(UUID id);
    CustomerResponse activateCustomer(UUID id);
}
