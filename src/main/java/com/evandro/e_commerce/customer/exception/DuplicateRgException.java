package com.evandro.e_commerce.customer.exception;

public class DuplicateRgException extends InvalidCustomerDataException {
    public DuplicateRgException(String message) {
        super(message);
    }
}