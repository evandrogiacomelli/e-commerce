package com.evandro.e_commerce.customer.exception;

public class DuplicateEmailException extends InvalidCustomerDataException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}