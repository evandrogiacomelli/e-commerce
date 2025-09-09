package com.evandro.e_commerce.customer.model;

public class CustomerAddress {
    private String zipCode;
    private String street;
    private int number;

    public CustomerAddress(String zipCode, String street, int number) {
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
    }
}
