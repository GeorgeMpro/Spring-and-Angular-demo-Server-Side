package george.fullstack.demo.springandangular.database;

import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CompanyRepository;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Company;
import george.fullstack.demo.springandangular.entity.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = SpringAndAngularApplication.class
)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CompanyMySQLIT {

    private Long testId;
    private final String testName = "test_name";
    private final String testEmail = "test@mail.com";
    private Company testCompany;

    @Autowired
    private CompanyRepository repository;

    @Autowired
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        couponRepository.deleteAll();
        testCompany = createTestCompany();
        createAndAddCouponsToCompany(testCompany);
        testCompany = repository.saveAndFlush(testCompany);
        testId = testCompany.getId();
    }


    @Test
    void getCompanyFromDB() {

        Company returnCompany = repository.findByName(testName).get();

        assertCompaniesHaveSameValues(testCompany, returnCompany);
    }

    @Test
    void updateCompany() {
        testCompany.setName("updated_name");
        testCompany.setPassword("0987");
        testCompany.setEmail("updated@mail.com");
        repository.saveAndFlush(testCompany);

        Company updated = repository.findById(testId).get();

        assertCompaniesHaveSameValues(testCompany, updated);
    }

    @Test
    void whenValueNotUnique_thenThrowException() {
        Company invalidName = new Company(testName, "some mail", "password", new ArrayList<>());
        Company invalidEmail = new Company("some name", testEmail, "password", new ArrayList<>());

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(invalidName));
        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(invalidEmail));
    }

    @Test
    void companyHasManyCoupons() {
        int couponListSize = testCompany.getCoupons().size();

        assertNotNull(testCompany.getCoupons());
        assertTrue(couponListSize != 0);
        assertThat("coupon list size should be greater than 0", couponListSize, greaterThan(0));
    }

    private Company createTestCompany() {
        testCompany = new Company();
        testCompany.setName(testName);
        testCompany.setEmail(testEmail);
        testCompany.setPassword("1234");

        return testCompany;
    }

    private void assertCompaniesHaveSameValues(Company expected, Company actual) {

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
    }

    private void createAndAddCouponsToCompany(Company company) {
        Coupon coupon = new Coupon("test1", "description1", "location1", LocalDate.now(), LocalDate.now(), null, new ArrayList<>());
        Coupon coupon2 = new Coupon("test2", "description2", "location2", LocalDate.now(), LocalDate.now(), null, new ArrayList<>());

        company.addCoupon(coupon);
        company.addCoupon(coupon2);
    }
}
