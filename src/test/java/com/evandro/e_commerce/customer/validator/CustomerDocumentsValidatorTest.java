package com.evandro.e_commerce.customer.validator;

import com.evandro.e_commerce.customer.exception.InvalidCpfException;
import com.evandro.e_commerce.customer.exception.InvalidCustomerDataException;
import com.evandro.e_commerce.customer.exception.InvalidRgException;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.validation.CustomerDocumentsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerDocumentsValidatorTest {

    private CustomerDocumentsValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CustomerDocumentsValidator();
    }

    @Test
    @DisplayName("Não deve lançar exceção para um documento válido")
    void shouldNotThrowExceptionForValidDocument() {
        var validDocuments = new CustomerDocuments("John Doe",
                LocalDate.of(1990, 5, 15),
                "111.222.333-44",
                "12.345.678-9");

        assertDoesNotThrow(() -> validator.validate(validDocuments));
    }

    @Test
    @DisplayName("Deve lançar InvalidCustomerDataException para documento nulo")
    void shouldThrowExceptionForNullDocument() {
        assertThrows(InvalidCustomerDataException.class, () -> {
            validator.validate(null);
        }, "Documents cannot be null");
    }

    @Test
    @DisplayName("Deve lançar InvalidCustomerDataException para nome nulo")
    void shouldThrowExceptionForNullName() {
        var documentsWithNullName = new CustomerDocuments(null,
                LocalDate.of(1990, 5, 15),
                "111.222.333-44",
                "12.345.678-9");

        assertThrows(InvalidCustomerDataException.class, () -> {
            validator.validate(documentsWithNullName);
        });
    }

    @Test
    @DisplayName("Deve lançar InvalidCpfException para CPF nulo")
    void shouldThrowExceptionForNullCpf() {
        var documentsWithNullCpf = new CustomerDocuments("Jane Doe",
                LocalDate.of(1995, 1, 1),
                null,
                "12.345.678-9");

        assertThrows(InvalidCpfException.class, () -> {
            validator.validate(documentsWithNullCpf);
        });
    }

    // Usando um teste parametrizado para testar vários CPFs inválidos de uma só vez
    @ParameterizedTest
    @ValueSource(strings = {"123.456.789-000", "11122233344", "abc.def.ghi-jk", "111.222.333-AB"})
    @DisplayName("Deve lançar InvalidCpfException para CPF com formato inválido")
    void shouldThrowExceptionForInvalidCpfFormat(String invalidCpf) {
        var documentsWithInvalidCpf = new CustomerDocuments("Jane Doe",
                LocalDate.of(1995, 1, 1),
                invalidCpf,
                "12.345.678-9");

        assertThrows(InvalidCpfException.class, () -> {
            validator.validate(documentsWithInvalidCpf);
        });
    }

    @Test
    @DisplayName("Deve lançar InvalidRgException para RG nulo")
    void shouldThrowExceptionForNullRg() {
        var documentsWithNullRg = new CustomerDocuments("Peter Pan",
                LocalDate.of(2000, 10, 20),
                "444.555.666-77",
                null);

        assertThrows(InvalidRgException.class, () -> validator.validate(documentsWithNullRg));
    }

    @Test
    @DisplayName("Deve lançar InvalidRgException para RG vazio ou em branco")
    void shouldThrowExceptionForEmptyOrBlankRg() {
        var documentsWithEmptyRg = new CustomerDocuments("Peter Pan",
                LocalDate.of(2000, 10, 20),
                "444.555.666-77",
                "");
        var documentsWithBlankRg = new CustomerDocuments("Peter Pan",
                LocalDate.of(2000, 10, 20),
                "444.555.666-77",
                "  ");


        assertThrows(InvalidRgException.class, () -> validator.validate(documentsWithEmptyRg));
        assertThrows(InvalidRgException.class, () -> validator.validate(documentsWithBlankRg));
    }
}
