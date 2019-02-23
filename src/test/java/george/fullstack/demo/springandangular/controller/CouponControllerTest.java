package george.fullstack.demo.springandangular.controller;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.service.CouponService;
import george.fullstack.demo.springandangular.service.CouponServiceImpl;
import george.fullstack.demo.springandangular.testhelper.CouponTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CouponControllerTest {

    private final String testName = "test1";
    private Coupon testCoupon;

    private CouponTestHelper helper;

    @Autowired
    private CouponService service;

    @Autowired
    private CouponRepository repository;

    @Autowired
    private CouponController controller;
    private long testId;

    @BeforeEach
    void setUp() {
        helper = new CouponTestHelper();
        repository.deleteAllInBatch();
        testCoupon = service.createCoupon(helper.createSimpleCoupon(testName));
        testId = testCoupon.getId();
    }

    @Test
    void getAllCoupons() {
        List<Coupon> coupons = controller.getAllCoupons();

        assertNotNull(coupons);
        assertNotEquals(Collections.emptyList(), coupons);
        assertTrue(coupons.size() > 0);
    }

    @Test
    void getCouponByName() {

        Coupon actual = controller.getCouponByName(testName);

        helper.assertSameCouponValues(testCoupon, actual);
    }

    @Test
    void whenNoSuchName_thenReturnException() {
        assertThrows(CouponServiceImpl.NoSuchCoupon.class, () ->
                controller.getCouponByName("no such name")
        );

    }

    @Test
    void getCouponById() {
        long id = testCoupon.getId();
        Coupon actual = controller.getCouponById(String.valueOf(id));

        helper.assertSameCouponValues(testCoupon, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindByIdThrowException() {
        long id = -1;
        assertThrows(CouponServiceImpl.NoSuchCoupon.class, () ->
                controller.getCouponById(String.valueOf(id))
        );
    }

    @Test
    void createCoupon() {
        String name = "create1";
        Coupon actual = controller.createCoupon(helper.createSimpleCoupon(name));
        Coupon expected = service.findByName(name);

        helper.assertSameCouponValues(expected, actual);
    }

    @Test
    void whenCreateDuplicateName_thenThrowException() {
        assertThrows(CouponServiceImpl.CouponAlreadyExist.class, () -> {
            Coupon duplicate = helper.createSimpleCoupon(testName);
            controller.createCoupon(duplicate);
        });
    }

    @Test
    void updateCoupon() {
        testCoupon.setName("update1");
        testCoupon.setDescription("updated description");
        testCoupon.setEndDate(LocalDate.now());

        controller.updateCoupon(testCoupon);
        Coupon updated = controller.getCouponById(String.valueOf(testId));

        helper.assertSameCouponValues(testCoupon, updated);
    }

    @Test
    void whenUpdateDuplicateName_thenThrowException() {
        assertThrows(CouponServiceImpl.CouponAlreadyExist.class, () -> {
            Coupon hasDuplicateName = controller.createCoupon(helper.createSimpleCoupon("test2"));
            hasDuplicateName.setName(testName);
            controller.updateCoupon(hasDuplicateName);
        });
    }

    @Test
    void deleteCoupon() {
        controller.deleteCouponById(testId);
        assertThrows(CouponServiceImpl.NoSuchCoupon.class, () ->
                controller.getCouponById(String.valueOf(testId))
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindCouponToDelete_thenThrowException(long id) {
        assertThrows(CouponServiceImpl.NoSuchCoupon.class, () -> controller.deleteCouponById(id));
    }
}
