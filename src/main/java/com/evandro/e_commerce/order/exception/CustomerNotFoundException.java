package com.evandro.e_commerce.order.exception;

public class CustomerNotFoundException extends RuntimeException {
        public CustomerNotFoundException(String message) {
            super(message);
        }

}
