package com.evandro.e_commerce.customer.validation;

import com.evandro.e_commerce.customer.exception.InvalidCpfException;
import com.evandro.e_commerce.customer.exception.InvalidCustomerDataException;
import com.evandro.e_commerce.customer.exception.InvalidRgException;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import org.springframework.stereotype.Component;

@Component
public class CustomerDocumentsValidator implements Validator<CustomerDocuments>{
    @Override
    public void validate(CustomerDocuments documents){
        if(documents == null){
            throw new InvalidCustomerDataException("Documents cannot be null");
        }
        if(documents.getName() == null || documents.getName().trim().isEmpty()){
            throw new InvalidCustomerDataException("Name cannot be null or empty");
        }
        if (documents.getCpf() == null || !isValidCpf(documents.getCpf())) {
            throw new InvalidCpfException("invalid cpf: " + documents.getCpf());
        }
        if (documents.getRg() == null || documents.getRg().trim().isEmpty()) {
            throw new InvalidRgException("RG cannot be null or empty");
        }
    }

    private static boolean isValidCpf(String cpf) {
        return cpf != null && cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }
}
