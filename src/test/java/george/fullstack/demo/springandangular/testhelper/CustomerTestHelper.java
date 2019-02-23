package george.fullstack.demo.springandangular.testhelper;

import com.google.gson.Gson;
import george.fullstack.demo.springandangular.dao.CustomerRepository;
import george.fullstack.demo.springandangular.entity.Customer;
import george.fullstack.demo.springandangular.service.CustomerService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTestHelper {
    public CustomerTestHelper() {
    }

    public Customer createSimpleCustomer(String name, String email) {
        Customer customer = new Customer(name, email, new ArrayList<>());
        customer.setId(0);

        return customer;
    }

    public Customer updateCustomerByName(CustomerService service, String name) {
        Customer updated = service.findCustomerByName(name);
        updated.setName("updated");
        updated.setEmail("updated@mail.com");

        return updated;
    }

    public Customer updateCustomer(CustomerRepository repository, Customer customer) {
        customer.setName("updated");
        customer.setEmail("updated@mail.com");

        return customer;
    }

    public void assertSameCustomerListValues(List<Customer> expected, List<Customer> actual) {
        assertAll("Comparing customer lists",
                () -> {
                    assertTrue(expected.size() > 0);
                    for (int i = 0; i < expected.size(); i++) {
                        assertSameCustomerValues(expected.get(i), actual.get(i));
                    }
                });
    }

    public void assertSameCustomerValues(Customer expected, Customer actual) {
        assertNotNull(expected);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getId(), actual.getId());
    }

    public String customerToJson(Customer testCustomer) {
        Gson gson = new Gson();
        return gson.toJson(testCustomer, Customer.class);
    }


}
