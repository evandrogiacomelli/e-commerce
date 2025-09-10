package com.evandro.e_commerce.customer.service;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    Customer create(CustomerDocuments documents, CustomerAddress address, CustomerRegisterInfo registerInfo);
    Optional<Customer> findById(UUID id);
    List<Customer> listAll();
    List<Customer> listActive();
    Customer update(UUID id, CustomerDocuments documents, CustomerAddress address, CustomerRegisterInfo registerInfo);
    Customer deactivate(UUID id);
    Customer activate(UUID id);
}
