package com.evandro.e_commerce.customer.validation;

public interface Validator<T> {
    void validate(T object);
}
