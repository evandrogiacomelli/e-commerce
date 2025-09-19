package com.evandro.e_commerce.customer.model;

import java.time.LocalDate;

public class CustomerDocuments {
    private String name;
    private LocalDate birthDate;
    private String cpf;
    private String rg;
    private String email;

    public CustomerDocuments() {}

    public CustomerDocuments(String name, LocalDate birthDate, String cpf, String rg) {
        this.name = name;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.rg = rg;
        this.email = null;
    }

    public CustomerDocuments(String name, LocalDate birthDate, String cpf, String rg, String email) {
        this.name = name;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.rg = rg;
        this.email = email;
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
}
