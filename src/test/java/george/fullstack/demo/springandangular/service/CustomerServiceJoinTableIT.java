package george.fullstack.demo.springandangular.service;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.dao.CustomerRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.entity.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CustomerServiceJoinTableIT {

    private String testName = "test1";
    private String testEmail = "test1@mail.com";
    private Customer testCustomer;

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        repository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
        testCustomer = returnSetupCustomer();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAllInBatch();
    }

    // todo (?)create spy methods to see that the repo is being called when needed
    @Test
    void getCustomersCoupons() {
        List<Coupon> returnedCoupons = customerService.getCouponsOwnedByThisCustomer(testCustomer);
        List<Coupon> testCoupons = testCustomer.getCoupons();

        assertNotNull(returnedCoupons);
        assertNotEquals(Collections.emptyList(), returnedCoupons);
        assertAll("coupon lists"
                , () -> {
                    assertEquals(testCoupons.size(), returnedCoupons.size());
                    for (int i = 0; i < testCoupons.size(); i++) {
                        assertSameCouponValues(testCoupons.get(i), returnedCoupons.get(i));
                    }
                });
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindByIdCustomerToGetCoupons_thenThrowException(long id) {
        String errorMessage = "Cannot get coupons. Customer not found. For id value: " + id;
        testCustomer.setId(id);
        Throwable throwable = assertThrows(CustomerServiceImpl.NoSuchCustomer.class, () -> customerService.getCouponsOwnedByThisCustomer(testCustomer));
        assertEquals(errorMessage, throwable.getMessage());
    }

    private Customer returnSetupCustomer() {
        Customer customer = createTestCustomer();
        return repository.saveAndFlush(customer);
    }

    private Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setName(testName);
        customer.setEmail(testEmail);
        Coupon coupon = createTestCoupon(testName, "description1", "location1");
        Coupon coupon2 = createTestCoupon("test2", "description2", "location2");
        customer.addCoupon(coupon);
        customer.addCoupon(coupon2);

        return customer;
    }

    private Coupon createTestCoupon(String name, String description, String location) {
        return new Coupon(name, description, location, LocalDate.now(), LocalDate.now(), null, new ArrayList<>());
    }

    private void assertSameCouponValues(Coupon expected, Coupon actual) {
        assertNotNull(expected);
        assertTrue(expected.getId() > 0);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getImageLocation(), actual.getImageLocation());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
        assertEquals(expected.getCompany(), actual.getCompany());
    }
}
