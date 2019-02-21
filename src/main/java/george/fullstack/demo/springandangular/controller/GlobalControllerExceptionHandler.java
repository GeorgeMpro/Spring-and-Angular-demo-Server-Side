package george.fullstack.demo.springandangular.controller;

import george.fullstack.demo.springandangular.service.CompanyServiceImpl;
import george.fullstack.demo.springandangular.service.CouponServiceImpl;
import george.fullstack.demo.springandangular.service.CustomerServiceImpl;
import george.fullstack.demo.springandangular.util.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = {
            CustomerServiceImpl.NoSuchCustomer.class,
            CouponServiceImpl.NoSuchCoupon.class,
            CompanyServiceImpl.NoSuchCompany.class})
    public ResponseEntity handleNoSuchEntityError(HttpServletRequest req, Exception ex) {

        ErrorResponse error = createErrorResponse(req, ex);

        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            CustomerServiceImpl.CustomerAlreadyExist.class,
            CouponServiceImpl.CouponAlreadyExist.class,
            CompanyServiceImpl.CompanyAlreadyExist.class})
    public ResponseEntity handleExistingEntityError(HttpServletRequest req, Exception ex) {

        ErrorResponse error = createErrorResponse(req, ex);

        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    //    todo(?) throw custom format exceptions from the different entities
    public ResponseEntity handleNumberFormatException(HttpServletRequest req, Exception ex) {

        ErrorResponse error = createErrorResponse(req, ex);

        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse createErrorResponse(HttpServletRequest req, Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setUrl(req.getRequestURI());
        error.setException(ex.getMessage());

        return error;
    }
}
