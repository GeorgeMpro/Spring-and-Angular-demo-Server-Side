package george.fullstack.demo.springandangular.service;

import george.fullstack.demo.springandangular.dao.CustomerRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository repository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository theRepository) {
        this.repository = theRepository;
    }

    @Override
    public List<Customer> findAllCustomers() {

        return repository.findAll();
    }

    @Override
    public Customer findById(long id) throws NoSuchCustomer {
        String errorMessage = "Customer not found. For id value: " + id;
        Optional<Customer> opt = repository.findById(id);

        return opt.orElseThrow(() -> new NoSuchCustomer(errorMessage));
    }

    @Override
    public Customer findCustomerByName(String name) throws NoSuchCustomer {
        String errorMessage = "Customer not found. For name value " + name;
        Optional<Customer> opt = repository.findByName(name);

        return opt.orElseThrow(() -> new NoSuchCustomer(errorMessage));
    }

    @Override
    public List<Coupon> getCouponsOwnedByThisCustomer(Customer customer) {
        long id = customer.getId();
        String errorMessage = "Cannot get coupons. Customer not found. For id value: " + id;
        if (!canFindById(id))
            throw new NoSuchCustomer(errorMessage);

        return repository.getCouponsOwnedByThisCustomer(id);
    }

    @Override
    public Customer createCustomer(Customer customer) {
        long id = customer.getId();
        String name = customer.getName();
        String errorMessage = "Cannot Create. Customer with this id or name already exists.";

        if (canFindById(id) | nameIsDuplicate(name))
            throw new CustomerAlreadyExist(errorMessage);

        return repository.saveAndFlush(customer);
    }

    @Override
    public void updateCustomer(Customer theCustomer) throws NoSuchCustomer {
        long id = theCustomer.getId();
        String cannotFindByIdErrorMessage = "Cannot update. Cannot find customer with id value " + id;
        String duplicateValueErrorMessage = "Cannot update. Customer with name or email values already exists.";

        if (!canFindById(id))
            throw new NoSuchCustomer(cannotFindByIdErrorMessage);

        try {
            repository.save(theCustomer);
        } catch (DataIntegrityViolationException e) {
            throw new CustomerAlreadyExist(duplicateValueErrorMessage);
        }
    }

    @Override
    public void deleteCustomerById(long id) throws NoSuchCustomer {
        String errorMessage = "Cannot delete. Cannot find customer with id value: " + id;

        if (!canFindById(id))
            throw new NoSuchCustomer(errorMessage);

        repository.deleteById(id);
    }

    @Override
    public void deleteAllCustomers() {
        repository.deleteAllInBatch();
    }

    private boolean canFindById(long id) {
        if (id < 1)
            return false;

        Optional<Customer> opt = repository.findById(id);
        return opt.isPresent();
    }

    private boolean nameIsDuplicate(String name) {
        Optional<Customer> opt = repository.findByName(name);
        return opt.isPresent();
    }


    public static class NoSuchCustomer extends RuntimeException {
        public NoSuchCustomer(String message) {
            super(message);
        }
    }

    public static class CustomerAlreadyExist extends RuntimeException {
        public CustomerAlreadyExist(String message) {
            super(message);
        }
    }
}
