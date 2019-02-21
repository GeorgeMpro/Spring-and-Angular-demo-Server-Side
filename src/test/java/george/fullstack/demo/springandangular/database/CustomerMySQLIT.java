package george.fullstack.demo.springandangular.database;

import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CustomerRepository;
import george.fullstack.demo.springandangular.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CustomerMySQLIT {

    private final String testName = "test";
    private final String testEmail = "test@test.com";
    private Customer testCustomer;

    @Autowired
    private CustomerRepository repository;
    private long testId;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        testCustomer = createTestCustomer();
        testCustomer = repository.save(testCustomer);
        testId = testCustomer.getId();
    }

    @Test
    void editCustomerInDb() {
        testCustomer.setName("updated");
        testCustomer.setEmail("updated@mail.com");
        repository.save(testCustomer);

        Customer updated = repository.findById(testId).get();

        assertSameCustomerValues(testCustomer, updated);
    }


    @Test
    void whenDuplicateValue_thenThrowException() {
        Customer duplicateName = new Customer(testName, "email", new ArrayList<>());
        Customer duplicateEmail = new Customer("name", testEmail, new ArrayList<>());

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(duplicateName));
        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(duplicateEmail));
    }

    private Customer createTestCustomer() {

        testCustomer = new Customer();
        testCustomer.setName(testName);
        testCustomer.setEmail(testEmail);

        return testCustomer;
    }

    //    todo(?) extract
    private void assertSameCustomerValues(Customer expected, Customer actual) {
        assertNotNull(expected);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
    }
}
