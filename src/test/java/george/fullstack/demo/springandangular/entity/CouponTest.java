package george.fullstack.demo.springandangular.entity;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.testhelper.CustomerTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CouponTest {

    private Coupon testCoupon;
    private String testName = "test1";
    private String description = "test1";
    private String location = "test_location";
    private String email = "test1@mail";
    private Customer testCustomer;

    private CustomerTestHelper customerHelper;

    @BeforeEach
    void setUp() {
        customerHelper = new CustomerTestHelper();
        testCustomer = customerHelper.createSimpleCustomer(testName, email);
        testCoupon = createTestCoupon();
    }

    @Test
    void creation() {
        assertNotNull(testCoupon);
        assertEquals(testName, testCoupon.getName());
        assertEquals(description, testCoupon.getDescription());
        assertEquals(location, testCoupon.getImageLocation());
        assertEquals(LocalDate.now(), testCoupon.getStartDate());
        assertEquals(LocalDate.now(), testCoupon.getEndDate());
        assertNotNull(testCoupon.getCompany());
    }

    @Test
    void couponHasManyCustomers() {
        List<Customer> customerList = testCoupon.getCustomers();
        Customer returnedCustomer = customerList.get(0);

        assertNotNull(testCoupon.getCustomers());
        assertNotEquals(Collections.emptyList(), testCoupon.getCustomers());
        assertTrue(testCoupon.getCustomers().size() > 1);
        assertEquals(testName, returnedCustomer.getName());
        assertEquals(email, returnedCustomer.getEmail());
    }

    private Coupon createTestCoupon() {
        Coupon coupon = new Coupon(testName, description, location, 1, LocalDate.now(), LocalDate.now(), new Company(), new ArrayList<>());
        Customer testCustomer2 = customerHelper.createSimpleCustomer("test2", "test2@mail.com");
        coupon.addCustomer(testCustomer);
        coupon.addCustomer(testCustomer2);
        return coupon;
    }
}
