package com.evandro.e_commerce.customer.repository;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.product.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer saveCustomer(Customer customer);
    Optional<Customer> findById(UUID id);
    List<Customer> findAll();
    List<Customer> findActiveCustomer();
    Customer updateCustomer(UUID id, CustomerDocuments documents, CustomerAddress address, CustomerRegisterInfo info);
    Customer deactiveCustomer(UUID id);
    Customer reactivateCustomer(UUID id);
}
