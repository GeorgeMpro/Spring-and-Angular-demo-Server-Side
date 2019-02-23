package george.fullstack.demo.springandangular.database;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.dao.CompanyRepository;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Company;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.testhelper.CompanyTestHelper;
import george.fullstack.demo.springandangular.testhelper.CouponTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CompanyCouponTableConnectionIT {
    // todo add tests for not returning the JSON object from the connected table - avoid infinite loop

    private final String testName = "test1";
    private final String testEmail = "test@mail";
    private Company testCompany;

    private CompanyTestHelper companyHelper;
    private CouponTestHelper couponHelper;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CouponRepository couponRepository;
    private long testId;

    @BeforeEach
    void setUp() {
        companyHelper = new CompanyTestHelper();
        couponHelper = new CouponTestHelper();
        companyRepository.deleteAll();
        couponRepository.deleteAll();
        testCompany = returnSetupCompanyFrom();
        testId = testCompany.getId();
    }


    @Test
    void couponsHaveOneCompany() {
        List<Coupon> coupons = couponRepository.findAll();

        assertNotNull(coupons);
        assertNotEquals(Collections.emptyList(), coupons);
        assertTrue(testId > 0);
        assertAll("coupons have one company",
                () -> {
                    for (Coupon c : coupons) {
                        Company tempCompany = c.getCompany();
                        assertEquals(testId, tempCompany.getId());
                        assertEquals(testName, tempCompany.getName());
                        assertEquals(testEmail, testCompany.getEmail());
                    }
                });
    }

    @Test
    void whenDeleteCompany_thenCouponsDeleted() {
        companyRepository.deleteAll();

        assertEquals(Collections.emptyList(), companyRepository.findAll());
        assertEquals(Collections.emptyList(), couponRepository.findAll());
    }

    @Test
    void whenDeleteAllCoupons_doesNotDeleteCompany() {
        couponRepository.deleteAllInBatch();
        Company company = companyRepository.findByName(testName).get();
        List<Company> returnedCompanies = companyRepository.findAll();

        assertEquals(Collections.emptyList(), couponRepository.findAll());
        assertNotNull(returnedCompanies);
        assertNotEquals(Collections.emptyList(), returnedCompanies);

        assertNotNull(company);
        assertEquals(testName, company.getName());
        assertEquals(testEmail, company.getEmail());
        assertEquals(testCompany.getPassword(), company.getPassword());
        assertEquals(Collections.emptyList(), company.getCoupons());
    }

    private Company returnSetupCompanyFrom() {
        Company company = setUpTestCompany();
        return companyRepository.saveAndFlush(company);
    }

    private Company setUpTestCompany() {
        Company tempCompany = companyHelper.createSimpleCompany(testName, testEmail);

        Coupon c1 = couponHelper.createSimpleCoupon("test1");
        Coupon c2 = couponHelper.createSimpleCoupon("test2");
        tempCompany.addCoupon(c1);
        tempCompany.addCoupon(c2);

        return tempCompany;
    }
}
