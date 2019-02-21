package george.fullstack.demo.springandangular.util;

import george.fullstack.demo.springandangular.entity.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class CouponDateFormatterTest {

    //    todo format exception tests
    private CouponDateFormatter couponDateFormatter;
    private Coupon testCoupon;
    private LocalDate now;

    @BeforeEach
    void setUp() {
        couponDateFormatter = new CouponDateFormatter();
        testCoupon = new Coupon();
        now = LocalDate.now();
    }

    @Test
    void currentDateToString() {
        String expectedDate = "" + now;
        String formattedDate = getDateToString(now);

        assertEquals(expectedDate, formattedDate);
    }

    @Test
    void formatDatesToString() {
        LocalDate dateToFormat = LocalDate.of(2019, 1, 4);
        String expectedDate = "2019-01-04";
        String myFormat = getDateToString(dateToFormat);

        assertEquals(expectedDate, myFormat);

        dateToFormat = LocalDate.of(1987, 4, 29);
        expectedDate = "1987-04-29";
        myFormat = getDateToString(dateToFormat);

        assertEquals(expectedDate, myFormat);
    }

    @Test
    void stringsToDates() {
        String stringToFormat = "2019-01-03";
        LocalDate date = LocalDate.of(2019, 1, 3);
        LocalDate myFormat = getLocalDate(stringToFormat);

        assertEquals(date, myFormat);

        stringToFormat = "2000-05-13";
        date = LocalDate.of(2000, 5, 13);
        myFormat = getLocalDate(stringToFormat);

        assertEquals(date, myFormat);
    }

    @Test
    void checkBeforeFormattedStrings() {
        String beforeToFormat = "2019-01-03";
        String afterToFormat = "2019-10-10";
        LocalDate before = getLocalDate(beforeToFormat);
        LocalDate after = getLocalDate(afterToFormat);

        assertTrue(before.isBefore(after));

        beforeToFormat = "1987-03-25";
        afterToFormat = "1987-03-26";
        before = getLocalDate(beforeToFormat);
        after = getLocalDate(afterToFormat);

        assertFalse(after.isBefore(before));
    }

    @Test
    void checkAfterFormattedStrings() {

        String beforeToFormat = "2019-01-03";
        String afterToFormat = "2019-10-10";
        LocalDate before = getLocalDate(beforeToFormat);
        LocalDate after = getLocalDate(afterToFormat);

        assertTrue(after.isAfter(before));

        beforeToFormat = "1987-03-25";
        afterToFormat = "1987-03-26";
        before = getLocalDate(beforeToFormat);
        after = getLocalDate(afterToFormat);

        assertTrue(after.isAfter(before));
    }

    @Test
    void couponDateToString() {
        LocalDate date = LocalDate.of(2018, 1, 4);

        testCoupon.setEndDate(date);
        testCoupon.setStartDate(date);

        String expected = "2018-01-04";
        String myEndDAte = getDateToString(testCoupon.getEndDate());
        String myStartDate = getDateToString(testCoupon.getStartDate());

        assertEquals(expected, myEndDAte);
        assertEquals(expected, myStartDate);
    }

    @Test
    void couponStringToDate() {

        String toFormat = "2018-01-04";
        LocalDate expectedDate = LocalDate.of(2018, 1, 4);

        LocalDate myFormat = getLocalDate(toFormat);

        testCoupon.setStartDate(myFormat);
        testCoupon.setEndDate(myFormat);

        assertEquals(expectedDate, testCoupon.getStartDate());
        assertEquals(expectedDate, testCoupon.getEndDate());
    }

    private String getDateToString(LocalDate toFormat) {
        return couponDateFormatter.dateToString(toFormat);
    }

    private LocalDate getLocalDate(String toFormat) {
        return couponDateFormatter.stringToDate(toFormat);
    }
}
