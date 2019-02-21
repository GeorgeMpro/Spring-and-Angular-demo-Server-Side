package george.fullstack.demo.springandangular.database;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CouponDateIT {
//    todo(?) change to parameterized tests

    @Autowired
    private CouponRepository repository;
    private final String couponName = "test1";

    @BeforeEach
    void setUp() {
        repository.deleteAllInBatch();
    }

    @Test
    void returnsDateNow() {
        LocalDate testDate = LocalDate.now();

        returnSetupCoupon(testDate);

        Optional<Coupon> optionalCoupon = repository.findByName(couponName);
        Coupon returned = optionalCoupon.get();

        assertEquals(testDate, returned.getStartDate());
        assertEquals(testDate, returned.getEndDate());
    }

    @Test
    void returnsMadeUpDate() {
        LocalDate testDate = LocalDate.of(2019, 2, 3);
        returnSetupCoupon(testDate);

        Optional<Coupon> optionalCoupon = repository.findByName(couponName);
        Coupon returned = optionalCoupon.get();

        assertEquals(testDate, returned.getStartDate());
        assertEquals(testDate, returned.getEndDate());
    }

    @Test
    void updateDateFields() {
        LocalDate now = LocalDate.now();
        LocalDate updatedDate = LocalDate.of(2010, 10, 23);

        Coupon coupon = returnSetupCoupon(now);

        coupon.setStartDate(updatedDate);
        coupon = repository.saveAndFlush(coupon);
        assertEquals(updatedDate, coupon.getStartDate());

        Optional<Coupon> optionalCoupon = repository.findByName(couponName);
        Coupon returned = optionalCoupon.get();

        assertEquals(updatedDate, returned.getStartDate());
    }

    @Test
    void testCouponListDates() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.of(2050, 3, 15);
        createCouponList(startDate, endDate);
        List<Coupon> queryCoupons = repository.findAll();

        assertNotNull(queryCoupons);
        assertNotEquals(Collections.emptyList(), queryCoupons);
        assertAll("coupon lists"
                , () -> {
                    for (Coupon c : queryCoupons) {
                        assertEquals(startDate, c.getStartDate());
                        assertEquals(endDate, c.getEndDate());
                    }
                });


    }


    private Coupon returnSetupCoupon(LocalDate testDate) {
        Coupon coupon = new Coupon();

        coupon.setName(couponName);
        coupon.setStartDate(testDate);
        coupon.setEndDate(testDate);
        coupon = repository.saveAndFlush(coupon);

        return coupon;
    }

    private void createCouponList(LocalDate startDate, LocalDate endDate) {
        List<Coupon> coupons = new ArrayList<>();
        Coupon c1 = new Coupon();

        c1.setStartDate(startDate);
        c1.setEndDate(endDate);
        c1.setName("c1");

        Coupon c2 = new Coupon();
        c2.setStartDate(startDate);
        c2.setEndDate(endDate);
        c2.setName("c2");

        Coupon c3 = new Coupon();
        c3.setEndDate(endDate);
        c3.setStartDate(startDate);
        c3.setName("c3");

        coupons.add(c1);
        coupons.add(c2);
        coupons.add(c3);

        repository.saveAll(coupons);
    }
}
