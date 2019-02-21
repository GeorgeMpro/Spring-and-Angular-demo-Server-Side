package george.fullstack.demo.springandangular.controller;

import george.fullstack.demo.springandangular.entity.Customer;
import george.fullstack.demo.springandangular.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers/")
//todo  remove @CrossOrigin when build for prod angular -  into the /dist folder
//todo add authentication in version 3
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    private CustomerService service;

    @Autowired
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping()
    public List<Customer> getAllCustomers() {

        return service.findAllCustomers();
    }

    @GetMapping("{name}/name")
    public Customer getCustomerByName(@PathVariable String name) {

        return service.findCustomerByName(name);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@RequestBody Customer customer) {

        service.createCustomer(customer);

        return customer;
    }

    @GetMapping("{id}/id")
    public Customer getCustomerById(@PathVariable(value = "id") String id) {

        long customerId = Long.valueOf(id);

        return service.findById(customerId);
    }

    @PutMapping
    public Customer updateCustomer(@RequestBody Customer customer) {

        service.updateCustomer(customer);

        return customer;
    }

    @DeleteMapping(path = "{id}/id")
    public void deleteCustomerById(@PathVariable String id) {
        long customerId = Long.valueOf(id);

        service.deleteCustomerById(customerId);
    }
}
