package george.fullstack.demo.springandangular.database;

import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CustomerRepository;
import george.fullstack.demo.springandangular.entity.Customer;
import george.fullstack.demo.springandangular.testhelper.CustomerTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CustomerMySQLIT {

    private long testId;
    private final String testName = "test";
    private final String testEmail = "test@test.com";
    private Customer testCustomer;

    private CustomerTestHelper helper;

    @Autowired
    private CustomerRepository repository;

    @BeforeEach
    void setUp() {
        helper = new CustomerTestHelper();
        repository.deleteAll();
        testCustomer = helper.createSimpleCustomer(testName, testEmail);
        testCustomer = repository.save(testCustomer);
        testId = testCustomer.getId();
    }

    @Test
    void updateCustomer() {
        repository.save(helper.updateCustomer(repository, testCustomer));

        Customer updated = repository.findById(testId).get();

        helper.assertSameCustomerValues(testCustomer, updated);
    }


    @Test
    void whenDuplicateName_throwException() {
        Customer duplicateName = helper.createSimpleCustomer(testName, "email");

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(duplicateName));
    }

    @Test
    void whenDuplicateEmail_throwExcetpion() {
        Customer duplicateEmail = helper.createSimpleCustomer("name", testEmail);
        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(duplicateEmail));
    }
}
