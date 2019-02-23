package george.fullstack.demo.springandangular.database;

import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.testhelper.CouponTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CouponDateIT {
    //    todo(?) change to parameterized tests
    private final String name = "test1";

    private CouponTestHelper helper;

    @Autowired
    private CouponRepository repository;
    private LocalDate now;

    @BeforeEach
    void setUp() {
        helper = new CouponTestHelper();
        repository.deleteAllInBatch();
        now = LocalDate.now();
    }

    @Test
    void returnsDateNow() {
        repository.save(helper.createCouponWithDateValues(name, now, now));

        Optional<Coupon> optionalCoupon = repository.findByName(name);
        Coupon returned = optionalCoupon.get();

        helper.assertCouponDateValues(now, returned);
    }


    @Test
    void returnsMadeUpDate() {
        LocalDate expected = LocalDate.of(2019, 2, 3);
        repository.save(helper.createCouponWithDateValues(name, expected, expected));

        Optional<Coupon> optionalCoupon = repository.findByName(name);
        Coupon returned = optionalCoupon.get();

        helper.assertCouponDateValues(expected, returned);
    }

    @Test
    void updateDateFields() {
        LocalDate updatedDate = LocalDate.of(2010, 10, 23);

        Coupon coupon = repository.save(helper.createCouponWithDateValues(name, now, now));

        coupon.setStartDate(updatedDate);
        coupon = repository.saveAndFlush(coupon);
        assertEquals(updatedDate, coupon.getStartDate());

        Optional<Coupon> optionalCoupon = repository.findByName(name);
        Coupon returned = optionalCoupon.get();

        assertEquals(updatedDate, returned.getStartDate());
    }

    @Test
    void testCouponListDates() {
        LocalDate endDate = LocalDate.of(2050, 3, 15);
        createCouponList(now, endDate);
        List<Coupon> coupons = repository.findAll();

        helper.assertCouponListDateValues(coupons, now, endDate);
    }

    private void createCouponList(LocalDate startDate, LocalDate endDate) {
        List<Coupon> coupons = new ArrayList<>();
        Coupon c1 = helper.createCouponWithDateValues("c1", startDate, endDate);
        Coupon c2 = helper.createCouponWithDateValues("c2", startDate, endDate);
        Coupon c3 = helper.createCouponWithDateValues("c3", startDate, endDate);

        coupons.add(c1);
        coupons.add(c2);
        coupons.add(c3);

        repository.saveAll(coupons);
    }
}
