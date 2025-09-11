package com.evandro.e_commerce.customer.dto;

import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.model.CustomerStatus;

public class CustomerDtoConverter {

    public static CustomerDocuments toCustomerDocuments(CustomerRequest request) {
        return new CustomerDocuments(
                request.getName(), 
                request.getBirthDate(), 
                request.getCpf(), 
                request.getRg());
    }

    public static CustomerAddress toCustomerAddress(CustomerRequest request) {
        return new CustomerAddress(
                request.getZipCode(), 
                request.getStreet(), 
                request.getNumber());
    }

    public static CustomerRegisterInfo toCustomerRegisterInfo() {
        return new CustomerRegisterInfo(CustomerStatus.ACTIVE);
    }
}