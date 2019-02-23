package george.fullstack.demo.springandangular.service;

import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CompanyRepository;
import george.fullstack.demo.springandangular.entity.Company;
import george.fullstack.demo.springandangular.testhelper.CompanyTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CompanyServiceIt {

    private CompanyTestHelper helper;
    //    todo(?) extract/delegate
    //    todo update names
    private String name1 = "test1";
    private String name2 = "test2";
    private String name3 = "test3";
    private String email1 = "test1@mail.com";
    private String email2 = "test2@mail.com";
    private String duplicateCreateErrorMessage = "Cannot create. Company with this id, name or email already exists";

    @Autowired
    private CompanyRepository repository;

    @Autowired
    private CompanyService service;

    @BeforeEach
    void setUp() {
        repository.deleteAllInBatch();
        helper = new CompanyTestHelper();
        createTestCompanies();
    }

    @Test
    void getAll() {
        List<Company> expected = repository.findAll();
        List<Company> actual = service.findAllCompanies();

        helper.assertEqualCompanyListValues(expected, actual);
    }

    @Test
    void create() {
        Company actual = helper.createCompanyForTest(service, name3, "specific@mail");
        Company expected = repository.findByName(name3).get();

        helper.assertEqualCompanyValues(expected, actual);
    }

    @Test
    void givenCreateDuplicateName_thenThrowException() {
        Throwable throwable = assertThrows(CompanyServiceImpl.CompanyAlreadyExist.class, () -> service.createCompany(helper.createSimpleCompany(name1, "unique")));
        assertErrorMessage(duplicateCreateErrorMessage, throwable);
    }

    @Test
    void givenCreateDuplicateEmail_thenThrowException() {
        Throwable throwable = assertThrows(CompanyServiceImpl.CompanyAlreadyExist.class, () -> service.createCompany(helper.createSimpleCompany("unique", email1)));
        assertErrorMessage(duplicateCreateErrorMessage, throwable);
    }

    @Test
    void givenCreateDuplicateId_thenThrowException() {
        Throwable throwable = assertThrows(CompanyServiceImpl.CompanyAlreadyExist.class, () -> {
            Company duplicate = helper.createSimpleCompany("unique", "unique@mail");
            duplicate.setId(helper.getTestCompanyID(repository, name1));
            service.createCompany(duplicate);
        });
        assertErrorMessage(duplicateCreateErrorMessage, throwable);
    }

    @Test
    void findById() {
        Company expected = helper.createCompanyForTest(service, name3, "specific@mail");
        Company actual = service.findById(expected.getId());

        helper.assertEqualCompanyValues(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {-10, 0, 9999})
    void whenCannotFindById_thenThrowException(long id) {
        Throwable throwable = assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.findById(id));
        String errorMessage = "Company not found. For id value: " + id;

        assertErrorMessage(errorMessage, throwable);
    }

    @Test
    void findByName() {
        Company expected = repository.findByName(name1).get();
        Company actual = service.findByName(name1);

        helper.assertEqualCompanyValues(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"noSuchName", "-1", "bad value"})
    void whenCannotFindByName_thenThrowException(String name) {
        String errorMessage = "Company not found. For name value " + name;
        Throwable throwable = assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.findByName(name));

        assertErrorMessage(errorMessage, throwable);
    }

    @Test
    void update() {
        Company expected = helper.updateExistingCompany(service, name1);
        Company actual = service.findById(expected.getId());

        helper.assertEqualCompanyValues(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindForUpdate_thenThrowException(long id) {
        String errorMessage = "Cannot Update. Company not found. For id value " + id;
        Company company = new Company();
        company.setId(id);
        Throwable throwable = assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.updateCompany(company));

        assertErrorMessage(errorMessage, throwable);
    }

    @Test
    void givenDuplicateNameCannotUpdate_thenThrowException() {
        Company duplicateName = service.findByName(name1);
        duplicateName.setName(name2);

        testForDuplicateUpdate(duplicateName);
    }

    @Test
    void givenDuplicateEmailCannotUpdate_thenThrowException() {
        Company duplicateEmail = service.findByName(name1);
        duplicateEmail.setEmail(email2);

        testForDuplicateUpdate(duplicateEmail);
    }

    @Test
    void delete() {
        helper.createCompanyForTest(service, name3, "specific@mail");
        long id = helper.getTestCompanyID(repository, name3);
        service.deleteCompanyById(id);

        assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.findById(id));
        assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.findByName(name3));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindIdForDelete_thenThrowException(long id) {
        String errorMessage = "Cannot Delete. Company not found. For id value: " + id;
        Throwable throwable = assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.deleteCompanyById(id));
        assertErrorMessage(errorMessage, throwable);
    }

    //    todo(?) extract/delegate
    private void createTestCompanies() {
        helper.createCompanyForTest(service, name1, email1);
        helper.createCompanyForTest(service, name2, email2);
    }

    private void testForDuplicateUpdate(Company duplicate) {
        Throwable throwable = assertThrows(CompanyServiceImpl.CompanyAlreadyExist.class, () -> service.updateCompany(duplicate));
        String duplicateErrorMessage = "Cannot update. Company with name or email values already exists.";
        assertErrorMessage(duplicateErrorMessage, throwable);
    }

    private void assertErrorMessage(String expected, Throwable throwable) {
        String actual = throwable.getMessage();
        assertEquals(expected, actual);
    }
}
