package com.evandro.e_commerce.order.exception;

public class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String message) {
            super(message);
        }

}
