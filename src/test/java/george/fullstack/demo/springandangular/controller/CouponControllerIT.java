package george.fullstack.demo.springandangular.controller;

import com.google.gson.Gson;
import george.fullstack.demo.springandangular.entity.Coupon;
import george.fullstack.demo.springandangular.service.CouponServiceImpl;
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
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CouponController.class)
@AutoConfigureMockMvc
public class CouponControllerIT {

    //    todo extract/delegate class
    private final String testName = "test1";
    private final String testDescription = "Test coupon";
    private final String testUrl = "url";

    private String errorMessage = "error";
    private final String baseUrlPath = "/coupons/";
    private final String getByNamePath = "/coupons/{name}/name";
    private final String getByIdPath = "/coupons/{id}/id";
    private MockHttpServletRequestBuilder requestBuilder;

    private final ResultMatcher ok = status().isOk();
    private final ResultMatcher notFound = status().isNotFound();
    private final ResultMatcher created = status().isCreated();
    private final ResultMatcher noContent = status().isNoContent();
    private final ResultMatcher badRequest = status().isBadRequest();

    private Coupon testCoupon;
    private String jsonTestCoupon;
    private long testId;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponController controller;

    @BeforeEach
    void setUp() {
        testCoupon = createTestCoupon(testName);
        jsonTestCoupon = couponToJson(testCoupon);
        testId = testCoupon.getId();
    }

    @Test
    void getAllCoupons() throws Exception {
        when(controller.getAllCoupons()).thenReturn(new ArrayList<>());

        requestBuilder = get(baseUrlPath);
        ResultMatcher ok = status().isOk();

        testPathStatus(requestBuilder, ok);
    }

    @Test
    void getCouponByName() throws Exception {
        when(controller.getCouponByName(testName)).thenReturn(testCoupon);

        requestBuilder = get(getByNamePath, testName);

        testPathStatusWithJson(requestBuilder, ok);

    }


    @Test
    void whenCannotFindByName_returnNotFound() throws Exception {
        String badName = "bad name";
        when(controller.getCouponByName(badName)).thenThrow(new CouponServiceImpl.NoSuchCoupon(errorMessage));

        requestBuilder = get(getByNamePath, badName);

        testPathStatusWithException(requestBuilder, notFound);
    }

    @Test
    void getCouponById() throws Exception {
        when(controller.getCouponById(any())).thenReturn(testCoupon);

        requestBuilder = get(getByIdPath, testId);
        testPathStatusWithContent(requestBuilder, ok);
    }

    @Test
    void whenCannotFindById_returnNotFound() throws Exception {
        when(controller.getCouponById(any())).thenThrow(new CouponServiceImpl.NoSuchCoupon(errorMessage));

        requestBuilder = get(getByIdPath, testId);
        testPathStatusWithException(requestBuilder, notFound);
    }

    @Test
    void createCoupon() throws Exception {
        when(controller.createCoupon(any())).thenReturn(testCoupon);

        requestBuilder = post(baseUrlPath);
        testPathStatusWithContent(requestBuilder, created);
    }

    @Test
    void whenCreateDuplicateName_thenReturnBadRequest() throws Exception {
        when(controller.createCoupon(any())).thenThrow(new CouponServiceImpl.CouponAlreadyExist(errorMessage));

        requestBuilder = post(baseUrlPath);
        ResultMatcher badRequest = status().isBadRequest();
        testPathStatusWithContentAndException(requestBuilder, badRequest);
    }

    @Test
    void updateCoupon() throws Exception {
        doNothing().when(controller).updateCoupon(testCoupon);

        requestBuilder = put(baseUrlPath);
        testPathStatusWithContent(requestBuilder, noContent);
    }

    @Test
    void whenCannotFindIdForUpdate_thenReturnNotFound() throws Exception {
        doThrow(new CouponServiceImpl.NoSuchCoupon(errorMessage)).when(controller).updateCoupon(any());

        requestBuilder = put(baseUrlPath);
        testPathStatusWithContentAndException(requestBuilder, notFound);
    }

    @Test
    void whenUpdateDuplicateName_thenReturnBadRequest() throws Exception {
        doThrow(new CouponServiceImpl.CouponAlreadyExist(errorMessage)).when(controller).updateCoupon(any());

        requestBuilder = put(baseUrlPath);
        testPathStatusWithContentAndException(requestBuilder, badRequest);
    }

    @Test
    void deleteCoupon() throws Exception {
        requestBuilder = delete(getByIdPath, testId);

        testPathStatus(requestBuilder, ok);
    }


    @Test
    void whenCannotFindForDelete_thenReturnNotFund() throws Exception {
        doThrow(new CouponServiceImpl.NoSuchCoupon(errorMessage)).when(controller).deleteCouponById(testId);

        requestBuilder = delete(getByIdPath, testId);
        testPathStatusWithException(requestBuilder, notFound);
    }

    private Coupon createTestCoupon(String name) {
        return new Coupon(name, testDescription, testUrl, null, null, null, new ArrayList<>());
    }

    //todo note: Test Coupon Date is null because of parsing error with jackson after parsing with gson (expecetd array/string)
//        test pass with postman. will need to reconfigure
    private String couponToJson(Coupon coupon) {
        Gson gson = new Gson();
        return gson.toJson(coupon, Coupon.class);
    }

    private ResultActions testPathStatus(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return mockMvc.perform(requestBuilder
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(expectedHttpStatus);
    }

    /**
     * The "exception" json path value is used for making sure the {@link GlobalControllerExceptionHandler} has this entities' exception mapped in one of its handlers.
     *
     * @param theRequestBuilder  - type of CRUD request
     * @param expectedHttpStatus
     * @throws Exception
     */
    private void testPathStatusWithException(MockHttpServletRequestBuilder theRequestBuilder, ResultMatcher expectedHttpStatus) throws Exception {

        testPathStatus(theRequestBuilder, expectedHttpStatus)
                .andExpect(jsonPath("exception", is(errorMessage)));
    }

    private ResultActions testPathStatusWithContent(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return mockMvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonTestCoupon))
                .andExpect(expectedHttpStatus);
    }

    private void testPathStatusWithContentAndException(MockHttpServletRequestBuilder theRequestBuilder, ResultMatcher expectedHttpStatus) throws Exception {

        testPathStatusWithContent(theRequestBuilder, expectedHttpStatus)
                .andExpect(jsonPath("exception", is(errorMessage)));
    }

    private ResultActions testPathStatusWithJson(MockHttpServletRequestBuilder theRequestBuilder, ResultMatcher expectedHttpStatus) throws Exception {
        return testPathStatus(theRequestBuilder, expectedHttpStatus)
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.name", is(testName)))
                .andExpect(jsonPath("$.description", is(testDescription)))
                .andExpect(jsonPath("$.imageLocation", is(testUrl)))
                .andExpect(jsonPath("$.startDate", nullValue()))
                .andExpect(jsonPath("$.endDate", nullValue()));
    }
}

