package george.fullstack.demo.springandangular.service;

import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.dao.CustomerRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.entity.Customer;
import george.fullstack.demo.springandangular.testhelper.CouponTestHelper;
import george.fullstack.demo.springandangular.testhelper.CustomerTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CustomerServiceJoinTableIT {

    private String testName = "test1";
    private String testEmail = "test1@mail.com";
    private Customer testCustomer;


    private CustomerTestHelper helper;
    private CouponTestHelper couponHelper;

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        helper = new CustomerTestHelper();
        couponHelper = new CouponTestHelper();
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
        List<Coupon> actual = customerService.getCouponsOwnedByThisCustomer(testCustomer);
        List<Coupon> expected = testCustomer.getCoupons();

        couponHelper.assertSameCouponListValues(expected, actual);
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
        Customer customer = helper.createSimpleCustomer(testName, testEmail);

        addCouponsToCustomer(customer);

        return repository.saveAndFlush(customer);
    }

    private Customer addCouponsToCustomer(Customer customer) {

        Coupon coupon = couponHelper.createSimpleCoupon(testName);
        Coupon coupon2 = couponHelper.createSimpleCoupon("test2");

        customer.addCoupon(coupon);
        customer.addCoupon(coupon2);

        return customer;
    }
}
