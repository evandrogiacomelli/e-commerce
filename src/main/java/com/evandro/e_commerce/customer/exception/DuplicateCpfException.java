package com.evandro.e_commerce.customer.exception;

public class DuplicateCpfException extends InvalidCustomerDataException {
    public DuplicateCpfException(String message) {
        super(message);
    }
}