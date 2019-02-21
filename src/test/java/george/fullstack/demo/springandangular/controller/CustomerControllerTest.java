package george.fullstack.demo.springandangular.controller;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

//todo update mock
public class CustomerControllerTest {

    private CustomerServiceMock service;

    private CustomerController controller;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        service = new CustomerServiceMock();
        controller = new CustomerController(service);
        testCustomer = createTestCustomer();
    }

    @Test
    void saveACustomer() {
        controller.createCustomer(testCustomer);

        Customer returned = controller.getCustomerByName(testCustomer.getName());

        assertSameCustomerValues(testCustomer, returned);
    }

    @Test
    void saveMultipleCustomers() {
        controller.createCustomer(testCustomer);

        List<Customer> returnedCustomers = controller.getAllCustomers();
        Customer returned = returnedCustomers.get(0);

        assertSameCustomerValues(testCustomer, returned);
    }

    @Test
    void returnsAllCustomers() {
        controller.createCustomer(testCustomer);
        List<Customer> returned = controller.getAllCustomers();

        assertNotNull(returned);
        assertNotEquals(Collections.emptyList(), returned);
    }

    //    todo update customer
    @Test
    void updateCustomer() {
        controller.createCustomer(createTestCustomer());
        long id = testCustomer.getId();

//        controller.updateCustomer(id);

        Customer returned = controller.getCustomerById(Long.toString(id));
        assertSameCustomerValues(testCustomer, returned);
    }

    //todo
    @Test
    void whenCannotUpdate_thenThrowException() {

    }

    //    todo add delete
    @Test
    void removeCustomer() {
        controller.createCustomer(testCustomer);
        long id = testCustomer.getId();
        service.deleteCustomerById(id);
        assertEquals(Collections.emptyList(), controller.getAllCustomers());
    }

    //todo(?) update to custom number format exception
    @ParameterizedTest
    @ValueSource(strings = {"invalid", "one", "11s"})
    void whenDeleteBadIdFormat_throwException(String invalidId) {
        assertThrows(NumberFormatException.class, () -> controller.deleteCustomerById(invalidId));
    }

    private Customer createTestCustomer() {
        Customer customer = new Customer("test1", "test1@mail.com", null);
        customer.setId(1L);
        return customer;
    }

    private void assertSameCustomerValues(Customer expected, Customer actual) {
        assertNotNull(expected);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
    }
}
