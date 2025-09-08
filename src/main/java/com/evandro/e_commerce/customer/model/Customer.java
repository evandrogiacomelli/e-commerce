package com.evandro.e_commerce.customer.model;

import java.util.List;
import java.util.UUID;

public class Customer {
    private UUID id;
    private CustomerDocuments documents;
    private CustomerAddress address;
    private CustomerRegisterInfo registerInfo;
    private List<CustomerHistory> history;
    private List<CustomerPaymentMethods> paymentMethods;

    public Customer(CustomerDocuments documents,
                    CustomerAddress address, CustomerRegisterInfo registerInfo) {
        this.id = UUID.randomUUID();
        this.documents = documents;
        this.address = address;
        this.registerInfo = registerInfo;
    }
}
