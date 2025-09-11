package com.evandro.e_commerce.customer.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerStatus;

public class CustomerResponse {

    private UUID id;
    private String name;
    private LocalDate birthDate;
    private String cpf;
    private String rg;
    private String zipCode;
    private String street;
    private int number;
    private LocalDateTime registerDate;
    private LocalDateTime lastAccess;
    private CustomerStatus status;
    private LocalDateTime inactiveIn;

    public CustomerResponse() {
    }

    public CustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getDocuments().getName();
        this.birthDate = customer.getDocuments().getBirthDate();
        this.cpf = customer.getDocuments().getCpf();
        this.rg = customer.getDocuments().getRg();
        this.zipCode = customer.getAddress().getZipCode();
        this.street = customer.getAddress().getStreet();
        this.number = customer.getAddress().getNumber();
        this.registerDate = customer.getRegisterInfo().getRegisterDate();
        this.lastAccess = customer.getRegisterInfo().getLastAccess();
        this.status = customer.getRegisterInfo().getStatus();
        this.inactiveIn = customer.getRegisterInfo().getInactiveIn();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRg() {
        return rg;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getStreet() {
        return street;
    }

    public int getNumber() {
        return number;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public LocalDateTime getInactiveIn() {
        return inactiveIn;
    }
}