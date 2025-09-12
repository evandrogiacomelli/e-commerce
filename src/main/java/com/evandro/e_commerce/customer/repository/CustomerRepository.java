package com.evandro.e_commerce.customer.repository;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerStatus;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    @Query("SELECT c FROM Customer c WHERE c.registerInfo.status = :status")
    List<Customer> findByRegisterInfoStatus(CustomerStatus status);
    
    default List<Customer> findActiveCustomers() {
        return findByRegisterInfoStatus(CustomerStatus.ACTIVE);
    }
}
