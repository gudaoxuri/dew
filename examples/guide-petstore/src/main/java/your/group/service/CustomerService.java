package your.group.service;

import com.tairanchina.csp.dew.core.service.CRUDSService;
import org.springframework.stereotype.Service;
import your.group.dao.CustomerDao;
import your.group.entity.Customer;

@Service
public class CustomerService implements CRUDSService<CustomerDao, Integer, Customer> {
}
