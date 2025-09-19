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

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.documents.cpf = :cpf")
    boolean existsByCpf(String cpf);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.documents.rg = :rg")
    boolean existsByRg(String rg);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.documents.email = :email")
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.documents.cpf = :cpf AND c.id != :id")
    boolean existsByCpfExcludingId(String cpf, UUID id);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.documents.rg = :rg AND c.id != :id")
    boolean existsByRgExcludingId(String rg, UUID id);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.documents.email = :email AND c.id != :id")
    boolean existsByEmailExcludingId(String email, UUID id);
}
