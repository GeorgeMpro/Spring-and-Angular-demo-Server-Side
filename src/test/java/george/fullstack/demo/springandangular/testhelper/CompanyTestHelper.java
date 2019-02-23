package george.fullstack.demo.springandangular.testhelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import george.fullstack.demo.springandangular.dao.CompanyRepository;
import george.fullstack.demo.springandangular.entity.Company;
import george.fullstack.demo.springandangular.service.CompanyService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompanyTestHelper {

    public Company createSimpleCompany(String name, String email) {
        return new Company(name, email, "1234", new ArrayList<>());
    }

    public Company createCompanyForTest(CompanyService service, String name, String email) {

        return service.createCompany(createSimpleCompany(name, email));
    }

    public Company updateExistingCompany(CompanyService service, String name) {
        Company toUpdate = service.findByName(name);
        toUpdate.setName("updated");
        toUpdate.setEmail("updated@mail");
        service.updateCompany(toUpdate);

        return toUpdate;
    }

    public long getTestCompanyID(CompanyRepository repository, String name) {
        Company company = repository.findByName(name).get();

        return company.getId();
    }


    public void assertEqualCompanyListValues(List<Company> expected, List<Company> actual) {
        assertTrue(actual.size() > 0);
        assertAll("List object comparison", () -> {
            for (int i = 0; i < expected.size(); i++) {
                assertEqualCompanyValues(expected.get(i), actual.get(i));
            }
        });
    }

    public void assertEqualCompanyValues(Company expected, Company actual) {
        assertNotNull(expected);
        assertTrue(expected.getId() > 0);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());

//todo compare the lists, they have different order on return
//        List<Coupon> expectedCoupons = expected.getCoupons();
//        List<Coupon> actualCoupons = actual.getCoupons();
    }

    /**
     * Converts object to JSON.
     * Excludes fields that are not marked with @Expose from the original object.
     *
     * @param company
     * @return
     */
    public String companyToJson(Company company) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(company, Company.class);
    }
}
