package your.group.service;

import com.ecfront.dew.core.service.CRUDSService;
import org.springframework.stereotype.Service;
import your.group.domain.Customer;

@Service
public class CustomerService implements CRUDSService<Customer.ActiveRecord, Integer, Customer> {
}
