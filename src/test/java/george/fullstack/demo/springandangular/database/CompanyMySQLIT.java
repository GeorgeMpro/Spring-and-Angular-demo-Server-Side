package george.fullstack.demo.springandangular.database;

import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CompanyRepository;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Company;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.testhelper.CompanyTestHelper;
import george.fullstack.demo.springandangular.testhelper.CouponTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    private final String testName = "test1";
    private final String testEmail = "test@mail.com";
    private Company testCompany;

    private CompanyTestHelper helper;

    private CouponTestHelper couponHelper;

    @Autowired
    private CompanyRepository repository;

    @Autowired
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        helper = new CompanyTestHelper();
        couponHelper = new CouponTestHelper();
        repository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
        testCompany = helper.createSimpleCompany(testName, testEmail);
        createAndAddCouponsToCompany(testCompany);
        testCompany = repository.saveAndFlush(testCompany);
        testId = helper.getTestCompanyID(repository, testName);
    }


    @Test
    void getCompanyFromDB() {
        Company returnCompany = repository.findByName(testName).get();

        helper.assertEqualCompanyValues(testCompany, returnCompany);
    }

    @Test
    void updateCompany() {
        testCompany.setName("updated_name");
        testCompany.setPassword("0987");
        testCompany.setEmail("updated@mail.com");
        repository.saveAndFlush(testCompany);

        Company updated = repository.findById(testId).get();

        helper.assertEqualCompanyValues(testCompany, updated);
    }

    @Test
    void whenCreateNameNotUnique_throwException() {
        Company duplicateName = helper.createSimpleCompany(testName, "mail");


        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(duplicateName));

    }

    @Test
    void whenCreateEmailNotUnique_throwException() {
        Company duplicateEmail = helper.createSimpleCompany("name", testEmail);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(duplicateEmail));
    }

    @Test
    void companyHasManyCoupons() {
        int couponListSize = testCompany.getCoupons().size();

        assertNotNull(testCompany.getCoupons());
        assertTrue(couponListSize != 0);
        assertThat("coupon list size should be greater than 0", couponListSize, greaterThan(0));
    }

    private void createAndAddCouponsToCompany(Company company) {
        Coupon c1 = couponHelper.createSimpleCoupon("test1");
        Coupon c2 = couponHelper.createSimpleCoupon("test2");

        company.addCoupon(c1);
        company.addCoupon(c2);
    }
}
