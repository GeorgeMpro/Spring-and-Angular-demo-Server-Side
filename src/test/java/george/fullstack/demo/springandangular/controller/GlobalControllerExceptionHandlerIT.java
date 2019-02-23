package george.fullstack.demo.springandangular.controller;


import com.google.gson.Gson;
import george.fullstack.demo.springandangular.entity.Customer;
import george.fullstack.demo.springandangular.service.CustomerServiceImpl;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GlobalControllerExceptionHandler.class)
@AutoConfigureMockMvc
class GlobalControllerExceptionHandlerIT {
//    todo (?)test controller - company, coupon
//    todo add format exception
//    todo add amount exception

    private MockMvc mockMvc;
    private final long testId = -1L;

    @MockBean
    private CustomerController controller;

    @Autowired
    private WebApplicationContext wac;

    private String errorMessage = "Error";
    private String customersBaseUrlPath = "/customers/";
    private String customersByIdPath = "/customers/{id}/id";
    private MockHttpServletRequestBuilder requestBuilder;
    private ResultMatcher notFound = status().isNotFound();
    private ResultMatcher badRequest = status().isBadRequest();
    private String jsonCustomer;


    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        Customer testCustomer = new Customer();
        jsonCustomer = customerToJson(testCustomer);
    }


    @Test
    void whenCannotFindById_returnBadRequest() {
        assertThrows(CustomerServiceImpl.NoSuchCustomer.class, () -> {
            when(controller.getCustomerById(any())).thenThrow(new CustomerServiceImpl.NoSuchCustomer(errorMessage));
            requestBuilder = get(customersByIdPath, testId);

            testPathWithJsonPathExceptionAndUrl(requestBuilder, notFound, getExpectedErrorUrlCustomerById(String.valueOf(testId)));

            verifyNoMoreInteractions(controller.getCustomerById(any()), times(1));
        });
    }

    @Test
    void whenCustomerAlreadyExist_returnBadRequest() {
        assertThrows(CustomerServiceImpl.CustomerAlreadyExist.class, () -> {
            when(controller.createCustomer(any())).thenThrow(new CustomerServiceImpl.CustomerAlreadyExist(errorMessage));
            requestBuilder = post(customersBaseUrlPath);

            testPathContentWithJsonPathExceptionAndUrl(requestBuilder, badRequest, customersBaseUrlPath);
            verify(controller.createCustomer(any()), times(1));
        });
    }

    @Test
    void whenBadCustomerIdFormat_thenReturnBadRequest() throws Exception {
        when(controller.getCustomerById(any())).thenThrow(new NumberFormatException(errorMessage));
        String badFormat = "badformat";
        requestBuilder = get(customersByIdPath, badFormat);

        testPathContentWithJsonPathExceptionAndUrl(requestBuilder, badRequest, getExpectedErrorUrlCustomerById(badFormat));
    }

    private ResultActions testPathWithJsonPathExceptionAndUrl(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus, String expectedUrl) throws Exception {
        return mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(expectedHttpStatus)
                .andExpect(jsonPath("exception", is(errorMessage)))
                .andExpect(jsonPath("url", is(expectedUrl)));
    }

    private ResultActions testPathContentWithJsonPathExceptionAndUrl(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus, String expectedUrl) throws Exception {
        return mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(jsonCustomer))
                .andExpect(expectedHttpStatus)
                .andExpect(jsonPath("exception", is(errorMessage)))
                .andExpect(jsonPath("url", is(expectedUrl)));
    }

    private String getExpectedErrorUrlCustomerById(String badFormat) {
        return customersBaseUrlPath + badFormat + "/id";
    }

    private String customerToJson(Customer testCustomer) {
        Gson gson = new Gson();
        return gson.toJson(testCustomer, Customer.class);
    }
}
