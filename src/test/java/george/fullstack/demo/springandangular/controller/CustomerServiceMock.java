package george.fullstack.demo.springandangular.controller;

import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.entity.Customer;
import george.fullstack.demo.springandangular.service.CustomerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//todo update or delete mock
public class CustomerServiceMock implements CustomerService {

    List<Customer> customers;

    @Override
    public List<Customer> findAllCustomers() {

        return customers;
    }

    @Override
    public Customer findCustomerByName(String name) {
        for (Customer c : customers) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Coupon> getCouponsOwnedByThisCustomer(Customer customer) {
        return null;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        if (customers == null) {
            customers = new ArrayList<>();
        }

        customers.add(customer);

        return customer;
    }

    @Override
    public void deleteCustomerById(long id) {
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            long customerId = customer.getId();
            if (customerId == id) {
                customers.remove(customer);
            }
        }
    }


    @Override
    public void deleteAllCustomers() {

        customers = Collections.emptyList();
    }

    @Override
    public void updateCustomer(Customer customer) {
    }

    @Override
    public Customer findById(long id) {
        for (Customer c : customers) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }
}
