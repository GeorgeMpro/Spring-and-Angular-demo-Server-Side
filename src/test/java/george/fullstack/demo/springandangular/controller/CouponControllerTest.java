package george.fullstack.demo.springandangular.controller;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.service.CouponService;
import george.fullstack.demo.springandangular.service.CouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CouponControllerTest {


    private final String testName = "test1";
    private Coupon testCoupon;

    @Autowired
    private CouponService service;

    @Autowired
    private CouponRepository repository;

    @Autowired
    private CouponController controller;
    private long testId;

    @BeforeEach
    void setUp() {
        repository.deleteAllInBatch();
        testCoupon = service.createCoupon(createTestCoupon(testName));
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

        Coupon returned = controller.getCouponByName(testName);

        assertSameCouponValues(testCoupon, returned);
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

        Coupon returned = controller.getCouponById(String.valueOf(id));

        assertSameCouponValues(testCoupon, returned);
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
        Coupon created = controller.createCoupon(createTestCoupon(name));
        Coupon fromServ = service.findByName(name);

        assertSameCouponValues(fromServ, created);
    }

    @Test
    void whenCreateDuplicateName_thenThrowException() {
        assertThrows(CouponServiceImpl.CouponAlreadyExist.class, () -> {
            Coupon duplicate = createTestCoupon(testName);
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

        assertSameCouponValues(testCoupon, updated);
    }

    @Test
    void whenUpdateDuplicateName_thenThrowException() {
        assertThrows(CouponServiceImpl.CouponAlreadyExist.class, () -> {
            Coupon hasDuplicateName = controller.createCoupon(createTestCoupon("test2"));
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

    private Coupon createTestCoupon(String name) {
        return new Coupon(name, "Test coupon", "url", LocalDate.now(), LocalDate.now(), null, new ArrayList<>());
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
