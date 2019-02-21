package george.fullstack.demo.springandangular.database;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.dao.CustomerRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CustomerCouponTableConnectionIT {

    private String customerName = "customer1";
    private String customerEmail = "test1@mail.com";
    private Customer testCustomer;
    private String couponName = "couponTest1";


    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private CouponRepository couponRepo;

    @BeforeEach
    void setUp() {
        couponRepo.deleteAllInBatch();
        customerRepo.deleteAllInBatch();
        testCustomer = returnSavedTestCustomer();
    }

    @Test
    void joinTableMapping() {
        List<Coupon> coupons = testCustomer.getCoupons();
        Coupon fromCustomer = coupons.get(0);

        Coupon fromRepo = couponRepo.findByName(couponName).get();

        assertSameCouponValues(fromRepo, fromCustomer);
    }

    @Test
    void whenCouponsAreDeleted_thenCustomerCouponsAreRemoved() {
        List<Coupon> beforeDelete = testCustomer.getCoupons();
        couponRepo.deleteAllInBatch();

        Customer after = customerRepo.findByName(customerName).get();
        List<Coupon> afterDelete = after.getCoupons();

        assertNotEquals(Collections.emptyList(), beforeDelete);
        assertEquals(Collections.emptyList(), afterDelete);
    }

    @Test
    void whenCustomerIsDeleted_thenCouponsIsNotDeleted() {
        Coupon testCoupon = returnSavedSetupCoupon();
        List<Customer> before = testCoupon.getCustomers();
        customerRepo.deleteAllInBatch();

        Coupon returnedCoupon = couponRepo.findByName(couponName).get();

        List<Customer> after = returnedCoupon.getCustomers();

        assertNotNull(before);
        assertNotEquals(Collections.emptyList(), before);

        assertEquals(couponName, returnedCoupon.getName());
        assertEquals(Collections.emptyList(), customerRepo.findAll());
        assertEquals(Collections.emptyList(), after);
    }

    private Customer returnSavedTestCustomer() {
        Customer customer = createTestCustomer(customerName, customerEmail);
        return customerRepo.saveAndFlush(customer);
    }

    private Customer createTestCustomer(String name, String email) {
        Customer testCustomer = new Customer(name, email, null);

        testCustomer = addCouponsToCustomer(testCustomer);

        return testCustomer;
    }

    private Customer addCouponsToCustomer(Customer customer) {

        Coupon coupon1 = new Coupon();
        Coupon coupon2 = new Coupon();
        coupon1.setName(couponName);
        coupon2.setName("couponTest2");
        customer.addCoupon(coupon1);
        customer.addCoupon(coupon2);

        return customer;
    }

    private Coupon returnSavedSetupCoupon() {
        Coupon coupon = createTestCoupon("test1");
        return couponRepo.saveAndFlush(coupon);
    }

    private Coupon createTestCoupon(String name) {
        Coupon coupon = new Coupon();
        coupon.setName(name);

        coupon = addCustomerToCoupon(coupon);

        return coupon;
    }

    private Coupon addCustomerToCoupon(Coupon coupon) {
        Customer customer = new Customer("name", "email@mail", new ArrayList<>());
        coupon.addCustomer(customer);

        return coupon;
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
