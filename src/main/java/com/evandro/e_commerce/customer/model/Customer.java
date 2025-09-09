package com.evandro.e_commerce.customer.model;

import java.util.ArrayList;
import java.util.UUID;

public class Customer {
    private UUID id;
    private CustomerDocuments documents;
    private CustomerAddress address;
    private CustomerRegisterInfo registerInfo;
    private ArrayList<CustomerHistory> history;
    private ArrayList<CustomerPaymentMethods> paymentMethods;

    public Customer(CustomerDocuments documents,
                    CustomerAddress address, CustomerRegisterInfo registerInfo) {
        this.id = UUID.randomUUID();
        this.documents = documents;
        this.address = address;
        this.registerInfo = registerInfo;
        this.history = new ArrayList<>();
        this.paymentMethods = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public CustomerDocuments getDocuments() {
        return documents;
    }

    public CustomerAddress getAddress() {
        return address;
    }

    public CustomerRegisterInfo getRegisterInfo() {
        return registerInfo;
    }

    public ArrayList<CustomerHistory> getHistory() {
        return history;
    }

    public ArrayList<CustomerPaymentMethods> getPaymentMethods() {
        return paymentMethods;
    }
}
