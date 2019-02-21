package george.fullstack.demo.springandangular.service;


import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Coupon;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CouponServiceIT {

    private final String testName = "test1";

    @Autowired
    private CouponRepository repository;

    @Autowired
    private CouponService service;

    private List<Coupon> testCoupons;

    @BeforeEach
    void setUp() {
        repository.deleteAllInBatch();
        testCoupons = repository.saveAll(createTestCouponsList());
    }

    @AfterEach
    void tearDown() {
        repository.deleteAllInBatch();
    }

    @Test
    void getAllCoupons() {
        List<Coupon> fromRepo = repository.findAll();
        List<Coupon> fromServ = service.findAllCoupons();

        assertTrue(fromServ.size() > 0);
        assertAll("Coupon list", () -> {
            for (int i = 0; i < fromRepo.size(); i++) {
                assertSameCouponValues(fromRepo.get(i), fromServ.get(i));
            }
        });
    }

    @Test
    void createCoupon() {
        String name = "test create";
        Coupon coupon = createTestCoupon(name);

        Coupon created = service.createCoupon(coupon);

        Optional<Coupon> optionalCoupon = repository.findByName(name);
        Coupon fromRepo = optionalCoupon.get();

        assertSameCouponValues(fromRepo, created);
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
        Coupon coupon = createTestCoupon(name);

        Coupon fromRepo = service.createCoupon(coupon);
        Coupon fromServ = service.findById(coupon.getId());

        assertSameCouponValues(fromRepo, fromServ);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 999})
    void whenFindNoSuchId_theThrowException(long id) {
        Throwable throwable = assertThrows(CouponServiceImpl.NoSuchCoupon.class, () -> service.findById(id));

        assertEquals("Coupon not found. For id value: " + id, throwable.getMessage());
    }

    @Test
    void findCouponByName() {
        Optional<Coupon> fromRepo = repository.findByName(testName);

        Coupon fromServ = service.findByName(testName);

        assertSameCouponValues(fromRepo.get(), fromServ);
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
            Coupon coupon = service.createCoupon(createTestCoupon(name));
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
        Coupon updated = service.createCoupon(createTestCoupon("To update"));
        updated.setName("updated name");
        service.updateCoupon(updated);

        Coupon returned = service.findById(updated.getId());

        assertSameCouponValues(updated, returned);
    }

    @Test
    void whenInvalidUpdateName_thenThrowException() {
        String errorMessage = "Cannot update. Coupon with name value " + testName + " already exists.";
        Throwable throwable = assertThrows(CouponServiceImpl.CouponAlreadyExist.class, () -> {
            Coupon duplicate = service.createCoupon(createTestCoupon("duplicate1"));
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

    private List<Coupon> createTestCouponsList() {
        List<Coupon> coupons = new ArrayList<>();
        Coupon c1 = createTestCoupon(testName);
        Coupon c2 = createTestCoupon("test2");

        coupons.add(c1);
        coupons.add(c2);

        return coupons;
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
