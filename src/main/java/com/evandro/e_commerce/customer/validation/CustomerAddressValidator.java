package com.evandro.e_commerce.customer.validation;

import com.evandro.e_commerce.customer.exception.InvalidAddressException;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import org.springframework.stereotype.Component;

@Component
public class CustomerAddressValidator implements Validator<CustomerAddress>{
    @Override
    public void validate(CustomerAddress address){
        if (address == null) {
            throw new InvalidAddressException("Address cannot be null");
        }
        if (address.getZipCode() == null || address.getZipCode().trim().isEmpty()) {
            throw new InvalidAddressException("ZIP code cannot be null or empty");
        }
        if (address.getStreet() == null || address.getStreet().trim().isEmpty()) {
            throw new InvalidAddressException("Street cannot be null or empty");
        }
    }
}
