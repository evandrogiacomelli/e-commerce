package com.evandro.e_commerce.customer.factory;

import com.evandro.e_commerce.customer.exception.InvalidAddressException;
import com.evandro.e_commerce.customer.exception.InvalidCpfException;
import com.evandro.e_commerce.customer.exception.InvalidCustomerDataException;
import com.evandro.e_commerce.customer.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class CustomerFactoryTest {

    CustomerDocuments validDocuments = new CustomerDocuments("John Doe",
            LocalDate.of(1990, 12, 5),
            "055.988.988-77", "10.444.234-2");

    CustomerAddress validAddress = new CustomerAddress("85300-200",
            "rua dos canarios", 44);

    CustomerRegisterInfo validRegisterInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);

    @Test
    @DisplayName("Should create customer with valid data through factory")
    void shouldCreateCustomerWithValidData() {
        Customer customer = CustomerFactory.create(validDocuments, validAddress, validRegisterInfo);
        assertNotNull(customer);
    }

    @Test
    @DisplayName("Should throw exception when address is null")
    void shouldThrowExceptionWhenAddressIsNull() {
        assertThrows(InvalidAddressException.class, () -> {
            CustomerFactory.create(validDocuments, null, validRegisterInfo);
        });
    }

    @Test
    @DisplayName("Should throw exception when registerInfo is null")
    void shouldThrowExceptionWhenRegisterInfoIsNull() {
        assertThrows(InvalidCustomerDataException.class, () -> {
            CustomerFactory.create(validDocuments, validAddress, null);
        });
    }

    @Test
    @DisplayName("Should throw exception when document is null")
    void shouldThrowExceptionWhenDocumentIsNull() {
        assertThrows(InvalidCustomerDataException.class, () -> {
            CustomerFactory.create(null, validAddress, validRegisterInfo);
        });
    }

    @Test
    @DisplayName("Should throw exception when CPF is invalid")
    void shouldThrowExceptionWhenCpfIsInvalid() {
        CustomerDocuments invalidDocuments = new CustomerDocuments("Evandro Giacomelli",
                LocalDate.of(1990, 12, 05), "111.222.333-000", "10.333.222-2");

        assertThrows(InvalidCpfException.class, () -> {
            CustomerFactory.create(invalidDocuments, validAddress, validRegisterInfo);
        });
    }
}
