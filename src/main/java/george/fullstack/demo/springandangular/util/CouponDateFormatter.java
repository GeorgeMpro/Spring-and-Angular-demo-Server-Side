package george.fullstack.demo.springandangular.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CouponDateFormatter {

    private final String datePattern = "yyyy-MM-dd";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);


    public String dateToString(LocalDate date) {

        return formatter.format(date);
    }

    public LocalDate stringToDate(String convertToDate) {

        return LocalDate.parse(convertToDate, formatter);
    }
}
