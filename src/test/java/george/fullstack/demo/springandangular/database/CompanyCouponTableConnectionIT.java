package george.fullstack.demo.springandangular.database;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.dao.CompanyRepository;
import george.fullstack.demo.springandangular.dao.CouponRepository;
import george.fullstack.demo.springandangular.entity.Company;
import george.fullstack.demo.springandangular.entity.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CompanyCouponTableConnectionIT {

    private final String testName = "testName";
    private final String testEmail = "test@mail";
    private Company testCompany;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CouponRepository couponRepository;
    private long testId;

    @BeforeEach
    void setUp() {
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
        Company tempCompany = new Company(testName, testEmail, "1234", null);

        Coupon coupon = new Coupon("test1", "description1", "location1", LocalDate.now(), LocalDate.now(), null, new ArrayList<>());
        Coupon coupon2 = new Coupon("test2", "description2", "location2", LocalDate.now(), LocalDate.now(), null, new ArrayList<>());
        tempCompany.addCoupon(coupon);
        tempCompany.addCoupon(coupon2);

        return tempCompany;
    }
}
