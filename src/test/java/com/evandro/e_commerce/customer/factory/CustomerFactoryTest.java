package com.evandro.e_commerce.customer.factory;

import com.evandro.e_commerce.customer.dto.CustomerCreationRequest;
import com.evandro.e_commerce.customer.exception.InvalidAddressException;
import com.evandro.e_commerce.customer.exception.InvalidCpfException;
import com.evandro.e_commerce.customer.model.*;
import com.evandro.e_commerce.customer.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class CustomerFactoryTest {

    @Mock
            private Validator<CustomerDocuments> documentsValidator;
    @Mock
            private Validator<CustomerAddress> addressValidator;

    private CustomerFactory customerFactory;

    private final CustomerDocuments validDocuments = new CustomerDocuments("John Doe",
            LocalDate.of(1990, 12, 5),
            "055.988.988-77", "10.444.234-2");

    private final CustomerAddress validAddress = new CustomerAddress("85300-200",
            "rua dos canarios", 44);

    private final CustomerRegisterInfo validRegisterInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);

    @BeforeEach
    void setUp() {
        customerFactory = new CustomerFactory(documentsValidator, addressValidator);
    }

    @Test
    @DisplayName("Should create customer successfully when all data is valid")
    void shouldCreateCustomerWithValidData() {
        var request = new CustomerCreationRequest(validDocuments, validAddress, validRegisterInfo);

        Customer customer = customerFactory.create(request);

        assertNotNull(customer);

        verify(documentsValidator).validate(validDocuments);
        verify(addressValidator).validate(validAddress);
    }


    @Test
    @DisplayName("Should throw exception when address validator fails")
    void shouldThrowExceptionWhenAddressIsValidatorFails() {
        var request = new CustomerCreationRequest(validDocuments, validAddress, validRegisterInfo);
        doThrow(new InvalidAddressException("Endereço nulo ou inválido")).when(addressValidator).validate(any(CustomerAddress.class));


        assertThrows(InvalidAddressException.class, () -> {
            customerFactory.create(request);
        });
    }

    @Test
    @DisplayName("Should throw exception when documents validator fails")
    void shouldThrowExceptionWhenDocumentValidatorFails() {
        var request = new CustomerCreationRequest(validDocuments, validAddress, validRegisterInfo);
        doThrow(new InvalidCpfException("CPF inválido")).when(documentsValidator).validate(any(CustomerDocuments.class));

        assertThrows(InvalidCpfException.class, () -> {
            customerFactory.create(request);
        });
    }

    @Test
    @DisplayName("Should throw exception when creating request With null registeInfo")
    void shouldThrowExceptionWhenCreatingRequestWithNullRegisterInfo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CustomerCreationRequest(validDocuments, validAddress, null);
        });
    }

    @Test
    @DisplayName("Should throw exception when creating request with null address")
    void shouldThrowExceptionWhenCreatingRequestWithNullAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CustomerCreationRequest(validDocuments, null, validRegisterInfo);
        });
    }

    @Test
    @DisplayName("Should throw exception when creating request with null documents")
    void shouldThrowExceptionWhenCreatingRequestWithNullDocuments() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CustomerCreationRequest(null, validAddress, validRegisterInfo);
        });
    }

}
