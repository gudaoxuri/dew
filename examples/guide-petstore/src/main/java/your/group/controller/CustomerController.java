package your.group.controller;

import com.tairanchina.csp.dew.core.controller.CRUDController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import your.group.entity.Customer;
import your.group.service.CustomerService;

@RestController
@RequestMapping("customer/")
@Api(description = "客户操作")
public class CustomerController implements CRUDController<CustomerService, Integer, Customer> {
}
