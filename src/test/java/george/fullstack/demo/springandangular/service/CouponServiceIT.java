package george.fullstack.demo.springandangular.service;


import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.testhelper.CouponTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CouponServiceIT {

    private final String testName = "test1";

    private CouponTestHelper helper;

    @Autowired
    private CouponRepository repository;

    @Autowired
    private CouponService service;


    @BeforeEach
    void setUp() {
        helper = new CouponTestHelper();
        repository.deleteAllInBatch();
        repository.saveAll(helper.createSimpleCouponList(testName));
    }

    @AfterEach
    void tearDown() {
        repository.deleteAllInBatch();
    }

    @Test
    void getAllCoupons() {
        List<Coupon> expected = repository.findAll();
        List<Coupon> actual = service.findAllCoupons();

        helper.assertSameCouponListValues(expected, actual);
    }


    @Test
    void createCoupon() {
        String name = "test create";
        Coupon coupon = helper.createSimpleCoupon(name);

        Coupon actual = service.createCoupon(coupon);

        Optional<Coupon> optionalCoupon = repository.findByName(name);
        Coupon expected = optionalCoupon.get();

        helper.assertSameCouponValues(expected, actual);
    }

    @Test
    void whenCreateDuplicateName_thenThrowException() {
        Throwable throwable = assertThrows(CouponServiceImpl.CouponAlreadyExist.class, () -> {
            Coupon coupon = new Coupon();
            coupon.setName(testName);
            service.createCoupon(coupon);
        });
        String errorMessage = "Cannot create. Coupon with name value: " + testName + " already exists";
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void findCouponById() {
        String name = "testFindById";

        Coupon expected = service.createCoupon(helper.createSimpleCoupon(name));
        Coupon actual = service.findByName(name);

        helper.assertSameCouponValues(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 999})
    void whenFindNoSuchId_theThrowException(long id) {
        Throwable throwable = assertThrows(CouponServiceImpl.NoSuchCoupon.class, () -> service.findById(id));

        assertEquals("Coupon not found. For id value: " + id, throwable.getMessage());
    }

    @Test
    void findCouponByName() {
        Optional<Coupon> expected = repository.findByName(testName);

        Coupon actual = service.findByName(testName);

        helper.assertSameCouponValues(expected.get(), actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"noSuchName", "-1", "bad value"})
    void ifCannotFindByName_thenThrowException(String name) {
        Throwable throwable = assertThrows(CouponServiceImpl.NoSuchCoupon.class, () -> service.findByName(name));

        String errorMessage = "Coupon not found. For name value: " + name;
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void deleteACoupon() {
        assertThrows(CouponServiceImpl.NoSuchCoupon.class, () -> {
            String name = "toDelete";
            Coupon coupon = service.createCoupon(helper.createSimpleCoupon(name));
            service.deleteCouponById(coupon.getId());
            service.findByName(name);
        });

    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void ifCannotDeleteById_thenThrowException(long id) {

        Throwable throwable = assertThrows(CouponServiceImpl.NoSuchCoupon.class, () -> service.deleteCouponById(id));
        String errorMessage = "Cannot Delete. Coupon not found. For id value: " + id;
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void updateCoupon() {
        Coupon expected = service.createCoupon(helper.createSimpleCoupon("To update"));
        expected.setName("updated name");
        service.updateCoupon(expected);

        Coupon actual = service.findById(expected.getId());

        helper.assertSameCouponValues(expected, actual);
    }

    @Test
    void whenInvalidUpdateName_thenThrowException() {
        String errorMessage = "Cannot update. Coupon with name value " + testName + " already exists.";
        Throwable throwable = assertThrows(CouponServiceImpl.CouponAlreadyExist.class, () -> {
            Coupon duplicate = service.createCoupon(helper.createSimpleCoupon("duplicate1"));
            duplicate.setName(testName);
            service.updateCoupon(duplicate);
        });

        assertEquals(errorMessage, throwable.getMessage());
    }


    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindForUpdate_thenThrowException(long id) {
        String errorMessage = "Cannot Update. Coupon not found. For id value " + id;
        Coupon coupon = new Coupon();
        coupon.setId(id);
        Throwable throwable = assertThrows(CouponServiceImpl.NoSuchCoupon.class, () -> service.updateCoupon(coupon));

        assertEquals(errorMessage, throwable.getMessage());
    }


}
