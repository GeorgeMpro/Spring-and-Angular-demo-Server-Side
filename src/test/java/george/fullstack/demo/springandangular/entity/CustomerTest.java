package george.fullstack.demo.springandangular.entity;

import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    @Test
    void creation() {
        Customer customer = new Customer("bob", "bob@bob.com", new ArrayList<>());

        assertEquals("bob", customer.getName());
        assertEquals("bob@bob.com", customer.getEmail());
        assertEquals(Collections.emptyList(), customer.getCoupons());
    }

    @Test
    void customerHasManyCoupons() {

        Customer customer = new Customer();
        customer.addCoupon(new Coupon());
        customer.addCoupon(new Coupon());
        List<Coupon> couponList = customer.getCoupons();

        assertNotNull(couponList);
        assertNotEquals(Collections.emptyList(), couponList);
        assertTrue(customer.getCoupons().size() > 1);
    }
}
