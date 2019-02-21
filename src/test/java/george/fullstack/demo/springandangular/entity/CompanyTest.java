package george.fullstack.demo.springandangular.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CompanyTest {

    private String testName = "TestInc";
    private String testEmail = "email";
    private String password = "1234";

    @Test
    void createCompany() {

        Company c = new Company(testName, testEmail, password, new ArrayList<>());

        assertEquals(testName, c.getName());
        assertEquals(testEmail, c.getEmail());
        assertEquals(password, c.getPassword());
    }

}
