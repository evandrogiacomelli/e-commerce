package com.evandro.e_commerce.customer.factory;

import com.evandro.e_commerce.customer.dto.CustomerCreationRequest;
import com.evandro.e_commerce.customer.exception.InvalidCustomerDataException;
import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.validation.Validator;
import org.springframework.stereotype.Component;

@Component
public class CustomerFactory {

    private final Validator<CustomerDocuments> documentsValidator;
    private final Validator<CustomerAddress> addressValidator;

    public CustomerFactory(Validator<CustomerDocuments> documentsValidator,
                           Validator<CustomerAddress> addressValidator){
        this.documentsValidator = documentsValidator;
        this.addressValidator = addressValidator;
    }

    public Customer create(CustomerCreationRequest request){
        CustomerDocuments documents = request.documents();
        CustomerAddress address = request.address();
        CustomerRegisterInfo registerInfo = request.registerInfo();

        documentsValidator.validate(documents);
        addressValidator.validate(address);

        return new Customer(documents, address, registerInfo);
    }

}
