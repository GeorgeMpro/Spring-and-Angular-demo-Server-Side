package george.fullstack.demo.springandangular.controller;

import com.google.gson.Gson;
import george.fullstack.demo.springandangular.entity.Company;
import george.fullstack.demo.springandangular.service.CompanyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CompanyController.class)
@AutoConfigureMockMvc
public class CompanyControllerIt {

    //    todo extract/delegate class
    private String testName = "test1";
    private String testPassword = "1234";
    private String testId = "0";
    private Company testCompany;
    private String jsonTestCompany;

    private String baseUrlPath = "/companies/";
    private String getByIdPath = baseUrlPath + "{id}/id";
    private String getByNamePath = baseUrlPath + "{name}/name";
    private final String errorMessage = "error";
    private MockHttpServletRequestBuilder requestBuilder;

    private ResultMatcher ok = status().isOk();
    private ResultMatcher notFound = status().isNotFound();
    private ResultMatcher badRequest = status().isBadRequest();
    private ResultMatcher created = status().isCreated();
    private ResultMatcher noContent = status().isNoContent();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyController controller;

    @BeforeEach
    void setUp() {
        testCompany = createTestCompany(testName, "test1@mail");
        jsonTestCompany = companyToJson(testCompany);
    }

    @Test
    void getAll() throws Exception {
        when(controller.getAllCompanies()).thenReturn(new ArrayList<>());
        requestBuilder = get(baseUrlPath);

        testResponsePathStatus(requestBuilder, ok);
    }

    @Test
    void getById() throws Exception {
        when(controller.getCompanyById(testId)).thenReturn(testCompany);
        requestBuilder = get(getByIdPath, testId);

        testResponsePathStatusWithJsonPathCompany(requestBuilder, ok);
    }


    @Test
    void whenCannotFindById_returnNotFound() throws Exception {
        when(controller.getCompanyById(any())).thenThrow(new CompanyServiceImpl.NoSuchCompany(errorMessage));
        requestBuilder = get(getByIdPath, testId);

        testResponsePathStatusWithExceptions(requestBuilder, notFound);
    }


    @Test
    void whenFindByBadIdFormat_thenThrowException() throws Exception {
        String invalidIdFormat = "invalid id";
        when(controller.getCompanyById(any())).thenThrow(new NumberFormatException(errorMessage));
        requestBuilder = get(getByIdPath, invalidIdFormat);

        testResponsePathStatusWithExceptions(requestBuilder, badRequest);
    }

    @Test
    void getByName() throws Exception {
        when(controller.getCompanyByName(testName)).thenReturn(testCompany);
        requestBuilder = get(getByNamePath, testName);

        testResponsePathStatusWithJsonPathCompany(requestBuilder, ok);
    }

    @Test
    void whenCannotGetByName_returnNotFound() throws Exception {
        String noSuchName = "no name";
        when(controller.getCompanyByName(any())).thenThrow(new CompanyServiceImpl.NoSuchCompany(errorMessage));
        requestBuilder = get(getByNamePath, noSuchName);

        testResponsePathStatusWithExceptions(requestBuilder, notFound);
    }

    @Test
    void create() throws Exception {
        when(controller.createCompany(any())).thenReturn(testCompany);
        requestBuilder = post(baseUrlPath);

        testPathStatusWithContent(requestBuilder, created);
    }

    @Test
    void whenCreateDuplicate_returnBadRequest() throws Exception {
        when(controller.createCompany(any())).thenThrow(new CompanyServiceImpl.CompanyAlreadyExist(errorMessage));
        requestBuilder = post(baseUrlPath);

        testResponsePathStatusWithContentException(requestBuilder, badRequest);
    }

    @Test
    void update() throws Exception {
        doNothing().when(controller).updateCompany(testCompany);

        requestBuilder = put(baseUrlPath);

        testPathStatusWithContent(requestBuilder, noContent);
    }

    @Test
    void whenUpdateBadFormat_thenReturnBadRequest() throws Exception {
        doThrow(new NumberFormatException(errorMessage)).when(controller).updateCompany(any());
        requestBuilder = put(baseUrlPath);

        testResponsePathStatusWithContentException(requestBuilder, badRequest);
    }

    @Test
    void whenCannotFindIdForUpdate_thenReturnNotFound() throws Exception {
        doThrow(new CompanyServiceImpl.NoSuchCompany(errorMessage)).when(controller).updateCompany(any());
        requestBuilder = put(baseUrlPath);

        testResponsePathStatusWithContentException(requestBuilder, notFound);

    }

    @Test
    void whenUpdateDuplicateValue_thenReturnBadRequest() throws Exception {
        doThrow(new CompanyServiceImpl.CompanyAlreadyExist(errorMessage)).when(controller).updateCompany(any());
        requestBuilder = put(baseUrlPath);

        testResponsePathStatusWithContentException(requestBuilder, badRequest);
    }

    @Test
    void deleteById() throws Exception {
        doNothing().when(controller).deleteCompanyById(any());
        requestBuilder = delete(getByIdPath, testId);

        testResponsePathStatus(requestBuilder, status().isOk());
    }

    @Test
    void whenCannotFindIdDelete_returnNotFound() throws Exception {
        doThrow(new CompanyServiceImpl.NoSuchCompany(errorMessage)).when(controller).deleteCompanyById(any());
        requestBuilder = delete(getByIdPath, testId);

        testResponsePathStatusWithExceptions(requestBuilder, notFound);
    }

    @Test
    void whenDeleteIdBadFormat_returnBadRequest() throws Exception {
        doThrow(new NumberFormatException(errorMessage)).when(controller).deleteCompanyById(any());
        requestBuilder = delete(getByIdPath, testId);

        testResponsePathStatusWithExceptions(requestBuilder, badRequest);
    }

    private ResultActions testResponsePathStatus(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(expectedHttpStatus);
    }

    private ResultActions testResponsePathStatusWithJsonPathCompany(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return testResponsePathStatus(requestBuilder, expectedHttpStatus)
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.name", is(testName)))
                .andExpect(jsonPath("$.password", is(testPassword)));
    }

    private ResultActions testResponsePathStatusWithExceptions(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return testResponsePathStatus(requestBuilder, expectedHttpStatus)
                .andExpect(jsonPath("exception", is(errorMessage)));
    }

    private ResultActions testPathStatusWithContent(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonTestCompany))
                .andExpect(expectedHttpStatus);
    }

    private ResultActions testResponsePathStatusWithContentException(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return testPathStatusWithContent(requestBuilder, expectedHttpStatus)
                .andExpect(jsonPath("exception", is(errorMessage)));
    }


    private Company createTestCompany(String name, String email) {

        return new Company(name, email, testPassword, new ArrayList<>());
    }

    private String companyToJson(Company company) {
        Gson gson = new Gson();
        return gson.toJson(company, Company.class);
    }
}
