package george.fullstack.demo.springandangular.service;

import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.entity.Customer;

import java.util.List;

public interface CustomerService {

    Customer createCustomer(Customer customer);

    void updateCustomer(Customer customer);

    List<Customer> findAllCustomers();

    Customer findCustomerByName(String name);

    Customer findById(long id);

    List<Coupon> getCouponsOwnedByThisCustomer(Customer customer);

    void deleteCustomerById(long id);

    void deleteAllCustomers();

}
