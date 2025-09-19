package com.evandro.e_commerce.customer.service;

import com.evandro.e_commerce.customer.dto.CustomerCreationRequest;
import com.evandro.e_commerce.customer.exception.DuplicateCpfException;
import com.evandro.e_commerce.customer.exception.DuplicateRgException;
import com.evandro.e_commerce.customer.exception.DuplicateEmailException;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.model.CustomerStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CustomerDuplicateValidationTest {

    @Autowired
    private CustomerService customerService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Test
    void shouldPreventDuplicateCpf() {
        CustomerDocuments docs1 = new CustomerDocuments("User 1", LocalDate.of(1990, 1, 1), "888.999.111-22", "88.999.111-0", "user1@test.com");
        CustomerAddress addr1 = new CustomerAddress("88888-888", "Street 1", 123);
        CustomerRegisterInfo info1 = new CustomerRegisterInfo(CustomerStatus.ACTIVE);

        customerService.createCustomer(new CustomerCreationRequest(docs1, addr1, info1));

        CustomerDocuments docs2 = new CustomerDocuments("User 2", LocalDate.of(1991, 2, 2), "888.999.111-22", "99.111.222-3", "user2@test.com");
        CustomerAddress addr2 = new CustomerAddress("54321-987", "Street 2", 456);
        CustomerRegisterInfo info2 = new CustomerRegisterInfo(CustomerStatus.ACTIVE);

        assertThrows(DuplicateCpfException.class, () ->
            customerService.createCustomer(new CustomerCreationRequest(docs2, addr2, info2))
        );
    }

    @Test
    void shouldPreventDuplicateRg() {
        CustomerDocuments docs1 = new CustomerDocuments("User 3", LocalDate.of(1992, 3, 3), "111.222.333-00", "11.222.333-0", "user3@test.com");
        CustomerAddress addr1 = new CustomerAddress("11111-111", "Street 3", 111);
        CustomerRegisterInfo info1 = new CustomerRegisterInfo(CustomerStatus.ACTIVE);

        customerService.createCustomer(new CustomerCreationRequest(docs1, addr1, info1));

        CustomerDocuments docs2 = new CustomerDocuments("User 4", LocalDate.of(1993, 4, 4), "555.666.777-88", "11.222.333-0", "user4@test.com");
        CustomerAddress addr2 = new CustomerAddress("22222-222", "Street 4", 222);
        CustomerRegisterInfo info2 = new CustomerRegisterInfo(CustomerStatus.ACTIVE);

        assertThrows(DuplicateRgException.class, () ->
            customerService.createCustomer(new CustomerCreationRequest(docs2, addr2, info2))
        );
    }

    @Test
    void shouldPreventDuplicateEmail() {
        CustomerDocuments docs1 = new CustomerDocuments("User 5", LocalDate.of(1994, 5, 5), "999.888.777-66", "22.333.444-5", "duplicate@test.com");
        CustomerAddress addr1 = new CustomerAddress("33333-333", "Street 5", 333);
        CustomerRegisterInfo info1 = new CustomerRegisterInfo(CustomerStatus.ACTIVE);

        customerService.createCustomer(new CustomerCreationRequest(docs1, addr1, info1));

        CustomerDocuments docs2 = new CustomerDocuments("User 6", LocalDate.of(1995, 6, 6), "333.444.555-77", "33.444.555-6", "duplicate@test.com");
        CustomerAddress addr2 = new CustomerAddress("44444-444", "Street 6", 444);
        CustomerRegisterInfo info2 = new CustomerRegisterInfo(CustomerStatus.ACTIVE);

        assertThrows(DuplicateEmailException.class, () ->
            customerService.createCustomer(new CustomerCreationRequest(docs2, addr2, info2))
        );
    }

    @Test
    void shouldAllowUniqueCustomer() {
        CustomerDocuments docs = new CustomerDocuments("Unique User", LocalDate.of(1996, 7, 7), "777.888.999-11", "44.555.666-7", "unique@test.com");
        CustomerAddress addr = new CustomerAddress("55555-555", "Unique Street", 555);
        CustomerRegisterInfo info = new CustomerRegisterInfo(CustomerStatus.ACTIVE);

        assertDoesNotThrow(() ->
            customerService.createCustomer(new CustomerCreationRequest(docs, addr, info))
        );
    }
}