package george.fullstack.demo.springandangular.controller;

import edu.emory.mathcs.backport.java.util.Collections;
import george.fullstack.demo.springandangular.dao.CompanyRepository;
import george.fullstack.demo.springandangular.entity.Company;
import george.fullstack.demo.springandangular.service.CompanyService;
import george.fullstack.demo.springandangular.service.CompanyServiceImpl;
import george.fullstack.demo.springandangular.testhelper.CompanyTestHelper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CompanyControllerTest {

    private final String testName1 = "test1";
    private final String testName2 = "test2";
    private final String testEmail1 = "test1@mail";
    private final String testEmail2 = "test2@mail";
    private Company testCompany1;
    private Company testCompany2;
    private long testId;

    private CompanyTestHelper helper;

    @Autowired
    private CompanyService service;

    @Autowired
    private CompanyRepository repository;

    @Autowired
    private CompanyController controller;

    @BeforeEach
    void setUp() {
        helper = new CompanyTestHelper();
        repository.deleteAllInBatch();
        testCompany1 = service.createCompany(helper.createSimpleCompany(testName1, testEmail1));
        testCompany2 = service.createCompany(helper.createSimpleCompany(testName2, testEmail2));
        testId = testCompany1.getId();
    }

    @Test
    void getAll() {
        List<Company> companies = controller.getAllCompanies();

        Assert.assertNotNull(companies);
        assertNotEquals(Collections.emptyList(), companies);
        assertTrue(companies.size() > 0);
    }

    @Test
    void getByName() {
        Company fromController = controller.getCompanyByName(testName1);

        helper.assertEqualCompanyValues(testCompany1, fromController);
    }

    @ParameterizedTest
    @ValueSource(strings = {"no name", ""})
    void whenCannotFindByName_throwException(String name) {
        assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> controller.getCompanyByName(name));
    }

    @Test
    void getById() {
        Company returned = controller.getCompanyById(String.valueOf(testId));

        helper.assertEqualCompanyValues(testCompany1, returned);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindById_thenThrowException(long id) {

        testCannotFindById(id);
    }

    @Test
    void create() {
        String name = "single create";
        String email = "single@mail";
        Company created = controller.createCompany(helper.createSimpleCompany(name, email));
        Company fromServ = service.findByName(name);

        helper.assertEqualCompanyValues(fromServ, created);
    }

    @Test
    void whenCreateDuplicateName_thenThrowException() {
        Company duplicateName = helper.createSimpleCompany(testName1, "not@duplicate");
        testDuplicateCreation(duplicateName);
    }

    @Test
    void whenCreateDuplicateEmail_thenThrowException() {
        Company duplicateEmail = helper.createSimpleCompany("not duplicate", testEmail1);
        testDuplicateCreation(duplicateEmail);
    }

    @Test
    void update() {
        testCompany1.setName("updated");
        testCompany1.setEmail("updated@mail");
        controller.updateCompany(testCompany1);
        Company updated = controller.getCompanyById(String.valueOf(testId));

        helper.assertEqualCompanyValues(testCompany1, updated);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindIdForUpdate_thenThrowException(long id) {
        testCompany1.setId(id);
        assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> controller.updateCompany(testCompany1));
    }

    @Test
    void whenUpdateDuplicateName_thenThrowException() {
        testCompany2.setName(testName1);
        testDuplicateUpdate(testCompany2);
    }

    @Test
    void whenUpdateDuplicateEmail_thenThrowException() {
        testCompany2.setEmail(testEmail1);
        testDuplicateUpdate(testCompany2);
    }

    @Test
    void delete() {
        controller.deleteCompanyById(String.valueOf(testId));
        testCannotFindById(testId);
    }

    private void testCannotFindById(long id) {
        assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.findById(id));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindCompanyToDelete_thenThrowException(long id) {
        assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> controller.deleteCompanyById(String.valueOf(id)));
    }

    //    todo(?) update with lambda
    private void testDuplicateCreation(Company duplicate) {
        assertThrows(CompanyServiceImpl.CompanyAlreadyExist.class, () -> controller.createCompany(duplicate));
    }

    private void testDuplicateUpdate(Company duplicate) {
        assertThrows(CompanyServiceImpl.CompanyAlreadyExist.class, () -> controller.updateCompany(duplicate));
    }
}
