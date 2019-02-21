package george.fullstack.demo.springandangular.service;

import george.fullstack.demo.springandangular.SpringAndAngularApplication;
import george.fullstack.demo.springandangular.dao.CompanyRepository;
import george.fullstack.demo.springandangular.entity.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringAndAngularApplication.class)
@TestPropertySource(locations = "classpath:application-mysql-test-connection.properties")
public class CompanyServiceIt {

    private final String testName2 = "test2";
    private final String testEmail2 = "test2@mail.com";
    @Autowired
    private CompanyRepository repository;

    @Autowired
    private CompanyService service;
    private List<Company> testCompanies;
    private String testName1 = "test1";
    private String testEmail = "test1@mail.com";

    @BeforeEach
    void setUp() {
        repository.deleteAllInBatch();
        testCompanies = repository.saveAll(createList());
    }

    @Test
    void getAll() {
        List<Company> fromRepo = repository.findAll();
        List<Company> fromServ = service.findAllCompanies();
        assertTrue(fromServ.size() > 0);
        assertAll("List object comparison", () -> {
            for (int i = 0; i < fromRepo.size(); i++) {
                assertSameCompanyValues(fromRepo.get(i), fromServ.get(i));
            }
        });
    }

    @Test
    void create() {
        String name = "test creation";
        Company company = createTestCompany(name, "creation@mail");
        Company created = service.createCompany(company);
        Company fromRepo = repository.findByName(name).get();

        assertSameCompanyValues(fromRepo, created);
    }

    @Test
    void whenCreateDuplicateName_thenThrowException() {
        Throwable throwable = assertThrows(CompanyServiceImpl.CompanyAlreadyExist.class, () -> {
            Company duplicate = createTestCompany(testName1, testEmail);
            service.createCompany(duplicate);
        });
        String errorMessage = "Cannot create. Company with this id, name or email already exists";
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void whenCreateDuplicateEmail_thenThrowException() {
        Throwable throwable = assertThrows(CompanyServiceImpl.CompanyAlreadyExist.class, () -> {
            Company duplicate = createTestCompany(testName1, testEmail);
            service.createCompany(duplicate);
        });
        String errorMessage = "Cannot create. Company with this id, name or email already exists";
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void whenCreateDuplicateId_thenThrowException() {
        Throwable throwable = assertThrows(CompanyServiceImpl.CompanyAlreadyExist.class, () -> {
            Company testCompany = service.findByName(testName1);
            Company duplicate = createTestCompany("unique", "unique@mail");
            duplicate.setId(testCompany.getId());
            service.createCompany(duplicate);
        });
        String errorMessage = "Cannot create. Company with this id, name or email already exists";
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void findById() {
        String name = "testFindById";
        Company company = createTestCompany(name, "find@id");

        Company fromRepo = service.createCompany(company);
        Company fromServ = service.findById(company.getId());
        assertSameCompanyValues(fromRepo, fromServ);
    }

    @ParameterizedTest
    @ValueSource(longs = {-10, 0, 9999})
    void whenCannotFindById_thenThrowException(long id) {
        Throwable throwable = assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.findById(id));
        String errorMessage = "Company not found. For id value: " + id;
        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void findByName() {
        Company fromRepo = repository.findByName(testName1).get();
        Company fromServ = service.findByName(testName1);

        assertSameCompanyValues(fromRepo, fromServ);
    }

    @ParameterizedTest
    @ValueSource(strings = {"noSuchName", "-1", "bad value"})
    void whenCannotFindByName_thenThrowException(String name) {
        String errorMessage = "Company not found. For name value " + name;
        Throwable throwable = assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.findByName(name));

        assertEquals(errorMessage, throwable.getMessage());
    }

    @Test
    void update() {
        Company toUpdate = service.findByName(testName1);
        toUpdate.setName("updated");
        toUpdate.setEmail("updated@mail");
        service.updateCompany(toUpdate);

        Company returned = service.findById(toUpdate.getId());

        assertSameCompanyValues(toUpdate, returned);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindForUpdate_thenThrowException(long id) {
        String errorMessage = "Cannot Update. Company not found. For id value " + id;
        Company company = new Company();
        company.setId(id);
        Throwable throwable = assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.updateCompany(company));

        assertEquals(errorMessage, throwable.getMessage());
    }


    @Test
    void whenCannotUpdateDuplicateName_thenThrowException() {
        Company duplicate = service.findByName(testName1);
        duplicate.setName(testName2);

        testForDuplicate(duplicate);
    }

    @Test
    void whenCannotUpdateDuplicateEmail_thenThrowException() {
        Company duplicate = service.findByName(testName1);
        duplicate.setEmail(testEmail2);

        testForDuplicate(duplicate);
    }

    @Test
    void delete() {
        String name = "single create";
        Company toRemove = service.createCompany(createTestCompany(name, "single@mail"));
        long id = toRemove.getId();

        service.deleteCompanyById(id);
        assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.findById(id));
        assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.findByName(name));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 9999})
    void whenCannotFindIdForDelete_thenThrowException(long id) {
        String errorMessage = "Cannot Delete. Company not found. For id value: " + id;
        Throwable throwable = assertThrows(CompanyServiceImpl.NoSuchCompany.class, () -> service.deleteCompanyById(id));
        assertEquals(errorMessage, throwable.getMessage());
    }

    private Company createTestCompany(String name, String email) {
        return new Company(name, email, "1234", new ArrayList<>());
    }

    private List<Company> createList() {
        List<Company> companies = new ArrayList<>();
        Company c1 = createTestCompany(testName1, testEmail);
        Company c2 = createTestCompany(testName2, testEmail2);
        companies.add(c1);
        companies.add(c2);
        return companies;
    }

    private void assertSameCompanyValues(Company expected, Company actual) {
        assertNotNull(expected);
        assertTrue(expected.getId() > 0);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertIterableEquals(expected.getCoupons(), actual.getCoupons());
    }

    private void testForDuplicate(Company duplicate) {
        String duplicateErrorMessage = "Cannot update. Company with name or email values already exists.";
        Throwable throwable = assertThrows(CompanyServiceImpl.CompanyAlreadyExist.class, () -> service.updateCompany(duplicate));
        assertEquals(duplicateErrorMessage, throwable.getMessage());
    }
}
