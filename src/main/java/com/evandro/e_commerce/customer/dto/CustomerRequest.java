package com.evandro.e_commerce.customer.dto;

import java.time.LocalDate;

public class CustomerRequest {

    private String name;
    private LocalDate birthDate;
    private String cpf;
    private String rg;
    private String email;
    private String zipCode;
    private String street;
    private int number;

    public CustomerRequest() {
    }

    public CustomerRequest(String name, LocalDate birthDate, String cpf, String rg, String zipCode, String street, int number) {
        this.name = name;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.rg = rg;
        this.email = null;
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
    }

    public CustomerRequest(String name, LocalDate birthDate, String cpf, String rg, String email, String zipCode, String street, int number) {
        this.name = name;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.rg = rg;
        this.email = email;
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
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

    public String getEmail() {
        return email;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}