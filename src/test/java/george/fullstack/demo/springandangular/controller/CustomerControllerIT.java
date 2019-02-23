package george.fullstack.demo.springandangular.controller;


import george.fullstack.demo.springandangular.entity.Customer;
import george.fullstack.demo.springandangular.service.CustomerServiceImpl;
import george.fullstack.demo.springandangular.testhelper.CustomerTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc
public class CustomerControllerIT {

    //    todo extract/delegate
    private Long testId = 0L;
    private String testName = "test1";
    private String testEmail = "test1@mail.com";
    private Customer testCustomer;
    private String testCustomerJson;

    private String baseUrlPath = "/customers/";
    private final String byNamePath = baseUrlPath + "{name}/name";
    private final String byIdPath = baseUrlPath + "{id}/id";

    private MockHttpServletRequestBuilder requestBuilder;
    private String errorMessage = "Error ";
    private ResultMatcher ok = status().isOk();
    private ResultMatcher notFound = status().isNotFound();
    private ResultMatcher created = status().isCreated();
    private ResultMatcher badRequest = status().isBadRequest();

    private CustomerTestHelper helper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerController controller;

    @BeforeEach
    void setUp() {
        helper = new CustomerTestHelper();
        testCustomer = helper.createSimpleCustomer(testName, testEmail);
        testCustomerJson = helper.customerToJson(testCustomer);
    }

    @Test
    void getAllCustomers() throws Exception {

        List<Customer> customers = Arrays.asList(
                helper.createSimpleCustomer(testName, testEmail),
                helper.createSimpleCustomer("test2", "test2@mail.com")
        );
        when(controller.getAllCustomers()).thenReturn(customers);
        requestBuilder = get(baseUrlPath);

        testResponseStatus(requestBuilder, ok)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].name", is(testName)))
                .andExpect(jsonPath("$[0].email", is(testEmail)));

        verify(controller, times(1)).getAllCustomers();
        verifyNoMoreInteractions(controller);
    }

    @Test
    void getCustomerByName() throws Exception {
        when(controller.getCustomerByName(any())).thenReturn(testCustomer);
        requestBuilder = get(byNamePath, testName);

        testResponseStatusWithJsonPathCustomer(requestBuilder, ok);
    }

    @Test
    void whenCannotFindByName_throwNoSuchCustomer() throws Exception {
        String badName = "no such name";
        when(controller.getCustomerByName(any())).thenThrow(new CustomerServiceImpl.NoSuchCustomer(errorMessage));
        requestBuilder = get(byNamePath, badName);

        testResponseStatusWithBodyContentAndExceptionJsonPath(requestBuilder, notFound);
    }

    @Test
    void createCustomer() throws Exception {
        when(controller.createCustomer(any())).thenReturn(testCustomer);
        requestBuilder = post(baseUrlPath);

        testResponseStatusWithBodyContent(requestBuilder, created)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    void whenCreateCustomerDuplicateValues_thenReturnBadRequest() throws Exception {
        when(controller.createCustomer(any())).thenThrow(new CustomerServiceImpl.CustomerAlreadyExist(errorMessage));
        requestBuilder = post(baseUrlPath);

        testResponseStatusWithBodyContentAndExceptionJsonPath(requestBuilder, badRequest);
    }


    @Test
    void whenCannotFindCustomerToUpdate_thenStatusIsNotFound() throws Exception {
        when(controller.updateCustomer(any())).thenThrow(new CustomerServiceImpl.NoSuchCustomer(errorMessage));
        requestBuilder = put(baseUrlPath);

        testResponseStatusWithBodyContentAndExceptionJsonPath(requestBuilder, notFound);
    }

    @Test
    void whenCustomerUpdateDuplicate_thenThrowException() throws Exception {
        when(controller.updateCustomer(any())).thenThrow(new CustomerServiceImpl.CustomerAlreadyExist(errorMessage));
        requestBuilder = put(baseUrlPath);

        testResponseStatusWithBodyContentAndExceptionJsonPath(requestBuilder, badRequest);
    }

    @Test
    void getCustomerById() throws Exception {
        when(controller.getCustomerById(testId.toString())).thenReturn(testCustomer);
        requestBuilder = get(byIdPath, testId);

        testResponseStatusWithJsonPathCustomer(requestBuilder, ok);
    }

    @Test
    void testGetCustomerByIdFormatException() throws Exception {
        String invalidId = "not a long";
        when(controller.getCustomerById(any())).thenThrow(new NumberFormatException(errorMessage));
        requestBuilder = get(byIdPath, invalidId);

        testResponseStatusWithExceptionJsonPath(requestBuilder, badRequest);
    }

    private ResultActions testResponseStatus(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedHttpStatus);
    }

    @Test
    void deleteACustomer() throws Exception {
        doNothing().when(controller).deleteCustomerById(any());
        requestBuilder = delete(byIdPath, testId);

        testResponseStatusWithBodyContent(requestBuilder, ok);
    }

    @Test
    void whenDeleteIdInBadFormat_thenBadRequest() throws Exception {
        String invalidId = "not a long";
        doThrow(new NumberFormatException(errorMessage)).when(controller).deleteCustomerById(any());
        requestBuilder = delete(byIdPath, invalidId);

        testResponseStatusWithExceptionJsonPath(requestBuilder, badRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "9999", "0"})
    void whenDeleteNonExistingCustomer_thenStatusIsNotFound(String id) throws Exception {
        doThrow(new CustomerServiceImpl.NoSuchCustomer(errorMessage)).when(controller).deleteCustomerById(any());
        requestBuilder = delete(byIdPath, id);

        testResponseStatusWithExceptionJsonPath(requestBuilder, notFound);

    }

    //    todo(?) extract/delegate class - general mvc helper class
    private ResultActions testResponseStatusWithBodyContent(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(testCustomerJson))
                .andExpect(expectedHttpStatus);
    }

    private ResultActions testResponseStatusWithBodyContentAndExceptionJsonPath(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return testResponseStatusWithBodyContent(requestBuilder, expectedHttpStatus)
                .andExpect(jsonPath("exception", is(errorMessage)));
    }

    private ResultActions testResponseStatusWithExceptionJsonPath(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return testResponseStatus(requestBuilder, expectedHttpStatus)
                .andExpect(jsonPath("exception", is(errorMessage)));
    }

    private ResultActions testResponseStatusWithJsonPathCustomer(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return testResponseStatus(requestBuilder, expectedHttpStatus)
                .andExpect(jsonPath("$.id", is(testId.intValue())))
                .andExpect(jsonPath("$.name", is(testName)))
                .andExpect(jsonPath("$.email", is(testEmail)));
    }
}
