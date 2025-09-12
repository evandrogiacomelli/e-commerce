package com.evandro.e_commerce.customer.dto;

import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;

public record CustomerCreationRequest(
        CustomerDocuments documents,
        CustomerAddress address,
        CustomerRegisterInfo registerInfo
) {
    public CustomerCreationRequest{
        if(documents == null || address == null || registerInfo == null){
            throw new IllegalArgumentException("Creation request fields cannot be null");
        }
    }
}
