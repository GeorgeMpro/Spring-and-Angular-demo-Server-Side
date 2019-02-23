package george.fullstack.demo.springandangular.service;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CustomerRepository;
import george.fullstack.demo.springandangular.entity.Customer;
import george.fullstack.demo.springandangular.testhelper.CustomerTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
class CustomerServiceIT {

    private String testName1 = "test1";
    private String testName2 = "test2";
    private String testMail1 = "bob@mail.com";
    private String testMail2 = "dan@mail.com";
    private List<Customer> testCustomers;

    private CustomerTestHelper helper;

    @Autowired
    private CustomerService service;

    @Autowired
    private CustomerRepository repository;


    @BeforeEach
    void setUp() {
        helper = new CustomerTestHelper();
        repository.deleteAllInBatch();
        testCustomers = repository.saveAll(createListOfTestCustomers());
    }

    @Test
    void create() {
        String testName = "testName";

        Customer expected = service.createCustomer(helper.createSimpleCustomer(testName, "test1@mail.com"));

        Customer actual = service.findCustomerByName(testName);
        helper.assertSameCustomerValues(expected, actual);
    }

    @Test
    void whenDuplicateIdCannotCreateCustomer_throwException() {
        Customer duplicateId = service.findCustomerByName(testName1);
        String errorMessage = "Cannot Create. Customer with this id or name already exists.";

        Throwable throwable = assertThrows(CustomerServiceImpl.CustomerAlreadyExist.class, () -> service.createCustomer(duplicateId));
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void whenDuplicateNameCannotCreateCustomer_throwException() {
        String errorMessage = "Cannot Create. Customer with this id or name already exists.";
        Customer duplicateName = helper.createSimpleCustomer(testName1, "not@duplicate");

        Throwable throwable = assertThrows(CustomerServiceImpl.CustomerAlreadyExist.class, () -> service.createCustomer(duplicateName));
        assertEquals(errorMessage, throwable.getMessage());

    }

    @Test
    void findAllCustomers() {
        List<Customer> fromRepo = repository.findAll();
        List<Customer> fromServ = service.findAllCustomers();

        helper.assertSameCustomerListValues(fromRepo, fromServ);
    }

    @Test
    void findCustomerByName() {
        Customer fromRepo = repository.findByName(testName1).get();
        Customer fromService = service.findCustomerByName(testName1);

        helper.assertSameCustomerValues(fromRepo, fromService);
    }

    @Test
    void whenCannotFindByName_throwNoSuchCustomerException() {

        String invalidName = "no such customer";
        String errorMessage = "Customer not found. For name value " + invalidName;
        Throwable throwable = assertThrows(CustomerServiceImpl.NoSuchCustomer.class, () -> service.findCustomerByName(invalidName));
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void findById() {
        Customer customer = service.createCustomer(helper.createSimpleCustomer("find", "find@mail.com"));

        Customer expected = service.findCustomerByName(customer.getName());
        Customer actual = service.findById(expected.getId());

        helper.assertSameCustomerValues(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 999})
    void whenFindNoSuchId_thenThrowException(long testId) {
        Throwable throwable = assertThrows(CustomerServiceImpl.NoSuchCustomer.class, () -> service.findById(testId));
        assertEquals(throwable.getMessage(), "Customer not found. For id value: " + testId);
    }

    @Test
    void deleteCustomer() {
        Customer toRemove = service.findCustomerByName(testName1);
        long customerId = toRemove.getId();
        service.deleteCustomerById(customerId);
        List<Customer> returnedList = service.findAllCustomers();
        assertTrue(testCustomers.size() > returnedList.size());
        assertThrows(CustomerServiceImpl.NoSuchCustomer.class, () -> service.findById(toRemove.getId()));
        assertThrows(CustomerServiceImpl.NoSuchCustomer.class, () -> service.findCustomerByName(toRemove.getName()));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenBadDeleteId_thenThrowNoSuchCustomer(long badId) {

        Throwable throwable = assertThrows(CustomerServiceImpl.NoSuchCustomer.class, () -> service.deleteCustomerById(badId));
        String errorMessage = "Cannot delete. Cannot find customer with id value: " + badId;
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void deleteAllCustomers() {
        assertNotEquals(Collections.emptyList(), service.findAllCustomers());

        service.deleteAllCustomers();

        List<Customer> returned = service.findAllCustomers();
        assertEquals(Collections.emptyList(), returned);
    }

    @Test
    void updateCustomer() {
        Customer updated = helper.updateCustomerByName(service, testName1);

        service.updateCustomer(updated);

        Customer returned = service.findCustomerByName(updated.getName());
        assertNotNull(returned);
        helper.assertSameCustomerValues(updated, returned);
    }


    @Test
    void whenCannotUpdate_thenThrowsException() {
        Customer badUpdate = helper.createSimpleCustomer("cannot", "update@mail.com");

        String errorMessage = "Cannot update. Cannot find customer with id value " + badUpdate.getId();
        Throwable throwable = assertThrows(CustomerServiceImpl.NoSuchCustomer.class, () -> service.updateCustomer(badUpdate));
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void whenUpdateDuplicateName_thenThrowsException() {
        Customer duplicate = service.findCustomerByName(testName2);
        duplicate.setName(testName1);
        checkForDuplicate(duplicate);
    }

    @Test
    void whenUpdateDuplicateEmail_thenThrowException() {
        Customer duplicate = service.findCustomerByName(testName2);
        duplicate.setEmail(testMail1);
        checkForDuplicate(duplicate);
    }

    private List<Customer> createListOfTestCustomers() {
        List<Customer> tempList = new ArrayList<>();

        tempList.add(helper.createSimpleCustomer(testName1, testMail1));
        tempList.add(helper.createSimpleCustomer(testName2, testMail2));
        tempList.add(helper.createSimpleCustomer("test3", "test3@mail.com"));

        return tempList;
    }

    private void checkForDuplicate(Customer duplicate) {
        String duplicateErrorMessage = "Cannot update. Customer with name or email values already exists.";
        Throwable throwable = assertThrows(CustomerServiceImpl.CustomerAlreadyExist.class, () -> service.updateCustomer(duplicate));
        assertEquals(duplicateErrorMessage, throwable.getMessage());
    }
}
