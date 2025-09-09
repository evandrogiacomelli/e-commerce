package com.evandro.e_commerce.customer.model;

import java.time.LocalDate;

public class CustomerDocuments {
    private String name;
    private LocalDate birthDate;
    private String cpf;
    private String rg;

    public CustomerDocuments(String name, LocalDate birthDate, String cpf, String rg) {
        this.name = name;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.rg = rg;
    }
}
