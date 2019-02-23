package george.fullstack.demo.springandangular.testhelper;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.entity.Coupon;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CouponTestHelper {

    public List<Coupon> createSimpleCouponList(String name) {
        List<Coupon> coupons = new ArrayList<>();
        Coupon c1 = createSimpleCoupon(name);
        Coupon c2 = createSimpleCoupon("test2");

        coupons.add(c1);
        coupons.add(c2);

        return coupons;
    }

    public Coupon createCouponWithDateValues(String name, LocalDate startDate, LocalDate endDate) {

        return new Coupon(name, null, null, 1, startDate, endDate, null, null);
    }

    public Coupon createSimpleCoupon(String name) {
        return new Coupon(name, "Test coupon", "url", 1, null, null, null, new ArrayList<>());
    }

    public void assertCouponDateValues(LocalDate expected, Coupon actual) {
        assertEquals(expected, actual.getStartDate());
        assertEquals(expected, actual.getEndDate());
    }

    public void assertCouponListDateValues(List<Coupon> actual, LocalDate start, LocalDate end) {
        assertNotNull(actual);
        assertNotEquals(Collections.emptyList(), actual);
        assertTrue(actual.size() > 0);
        assertAll("coupon lists"
                , () -> {
                    for (Coupon c : actual) {
                        assertEquals(start, c.getStartDate());
                        assertEquals(end, c.getEndDate());
                    }
                });
    }

    public void assertSameCouponListValues(List<Coupon> expected, List<Coupon> actual) {
        assertAll("Coupon list", () -> {
            assertNotNull(expected);
            assertTrue(actual.size() > 0);
            for (int i = 0; i < expected.size(); i++) {
                assertSameCouponValues(expected.get(i), actual.get(i));
            }
        });
    }

    public void assertSameCouponValues(Coupon expected, Coupon actual) {
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
