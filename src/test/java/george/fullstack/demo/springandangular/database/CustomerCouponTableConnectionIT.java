package george.fullstack.demo.springandangular.database;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.dao.CustomerRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.entity.Customer;
import george.fullstack.demo.springandangular.testhelper.CouponTestHelper;
import george.fullstack.demo.springandangular.testhelper.CustomerTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CustomerCouponTableConnectionIT {

    private String name = "test1";
    private String email = "test1@mail.com";
    private Customer testCustomer;
    private String couponName = "couponTest1";

    private CustomerTestHelper customerHelper;

    private CouponTestHelper couponHelper;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private CouponRepository couponRepo;

    @BeforeEach
    void setUp() {
        customerHelper = new CustomerTestHelper();
        couponHelper = new CouponTestHelper();
        couponRepo.deleteAllInBatch();
        customerRepo.deleteAllInBatch();
        testCustomer = returnSavedTestCustomer();
    }

    @Test
    void joinTableMapping() {
        List<Coupon> coupons = testCustomer.getCoupons();
        Coupon actual = coupons.get(0);

        Coupon expected = couponRepo.findByName(couponName).get();

        couponHelper.assertSameCouponValues(expected, actual);
    }

    @Test
    void whenCouponsAreDeleted_thenCustomerCouponsAreRemoved() {
        List<Coupon> beforeDelete = testCustomer.getCoupons();
        couponRepo.deleteAllInBatch();

        Customer after = customerRepo.findByName(name).get();
        List<Coupon> afterDelete = after.getCoupons();

        assertNotEquals(Collections.emptyList(), beforeDelete);
        assertEquals(Collections.emptyList(), afterDelete);
    }

    @Test
    void whenCustomerIsDeleted_thenCouponsIsNotDeleted() {
        Coupon testCoupon = returnSavedSetupCoupon();
        List<Customer> before = testCoupon.getCustomers();
        customerRepo.deleteAllInBatch();

        Coupon actual = couponRepo.findByName(couponName).get();

        List<Customer> after = actual.getCustomers();

        assertNotNull(before);
        assertNotEquals(Collections.emptyList(), before);

        assertEquals(couponName, actual.getName());
        assertEquals(Collections.emptyList(), customerRepo.findAll());
        assertEquals(Collections.emptyList(), after);
    }

    private Customer returnSavedTestCustomer() {
        Customer customer = customerHelper.createSimpleCustomer(name, email);

        addCouponsToCustomer(customer);

        return customerRepo.saveAndFlush(customer);
    }

    private Customer addCouponsToCustomer(Customer customer) {

        Coupon coupon1 = couponHelper.createSimpleCoupon(couponName);
        Coupon coupon2 = couponHelper.createSimpleCoupon("couponTest2");

        customer.addCoupon(coupon1);
        customer.addCoupon(coupon2);

        return customer;
    }

    private Coupon returnSavedSetupCoupon() {
        Coupon coupon = couponHelper.createSimpleCoupon(name);

        addCustomerToCoupon(coupon);

        return couponRepo.saveAndFlush(coupon);
    }

    private Coupon addCustomerToCoupon(Coupon coupon) {
        Customer customer = customerHelper.createSimpleCustomer("name", "email@mail");

        coupon.addCustomer(customer);

        return coupon;
    }
}
