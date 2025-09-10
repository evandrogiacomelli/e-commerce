package com.evandro.e_commerce.customer.service;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    Customer createCustomer(CustomerDocuments documents, CustomerAddress address, CustomerRegisterInfo registerInfo);
    Optional<Customer> findCustomerById(UUID id);
    List<Customer> listAllCustomer();
    List<Customer> listActiveCustomer();
    Customer updateCustomer(UUID id, CustomerDocuments documents, CustomerAddress address, CustomerRegisterInfo registerInfo);
    Customer deactivateCustomer(UUID id);
    Customer activateCustomer(UUID id);
}
