package com.evandro.e_commerce.customer.factory;

import com.evandro.e_commerce.customer.exception.InvalidAddressException;
import com.evandro.e_commerce.customer.exception.InvalidCpfException;
import com.evandro.e_commerce.customer.exception.InvalidCustomerDataException;
import com.evandro.e_commerce.customer.exception.InvalidRgException;
import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;

public class CustomerFactory {

    public static Customer create(CustomerDocuments documents,
                                  CustomerAddress address,
                                  CustomerRegisterInfo registerInfo) {
        validateInputs(documents, address, registerInfo);
        validateDocuments(documents);
        validateAddress(address);

        return new Customer(documents, address, registerInfo);
    }

    private static void validateInputs(CustomerDocuments documents,
                                       CustomerAddress address,
                                       CustomerRegisterInfo registerInfo) {
        if (documents == null) {
            throw new InvalidCustomerDataException("Documents cannot be null");
        }
        if (address == null) {
            throw new InvalidAddressException("Address cannot be null");
        }
        if (registerInfo == null) {
            throw new InvalidCustomerDataException("Register info cannot be null");
        }
    }

    private static void validateDocuments(CustomerDocuments documents) {
        if (documents.getName() == null || documents.getName().trim().isEmpty()) {
            throw new InvalidCustomerDataException("Name cannot be null or empty");
        }
        if (documents.getCpf() == null || !isValidCpf(documents.getCpf())) {
            throw new InvalidCpfException("invalid cpf: " + documents.getCpf());
        }
        if (documents.getRg() == null || documents.getRg().trim().isEmpty()) {
            throw new InvalidRgException("RG cannot be null or empty");
        }
    }

    private static void validateAddress(CustomerAddress address) {
        if (address.getZipCode() == null || address.getZipCode().trim().isEmpty()) {
            throw new InvalidAddressException("ZIP code cannot be null or empty");
        }
        if (address.getStreet() == null || address.getStreet().trim().isEmpty()) {
            throw new InvalidAddressException("Street cannot be null or empty");
        }
    }

    private static boolean isValidCpf(String cpf) {
        return cpf != null && cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }

}
