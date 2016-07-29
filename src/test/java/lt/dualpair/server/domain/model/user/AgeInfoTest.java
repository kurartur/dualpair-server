package lt.dualpair.server.domain.model.user;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.*;

public class AgeInfoTest {

    @Test
    public void testAgeCalculation_futureDate() throws Exception {
        LocalDate localDate = LocalDate.now().plus(1, ChronoUnit.DAYS);
        try {
            new AgeInfo(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Can't be born in the future", iae.getMessage());
        }
    }

    @Test
    public void testAgeCalculation() throws Exception {
        LocalDate localDate = LocalDate.now().minus(5, ChronoUnit.YEARS);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        AgeInfo ageInfo = new AgeInfo(date);
        assertEquals((Integer)5, ageInfo.getAge());
        assertEquals(date, ageInfo.getDateOfBirth());
    }

    @Test
    public void testNullConstructor() throws Exception {
        AgeInfo ageInfo = new AgeInfo(null);
        assertNull(ageInfo.getAge());
        assertNull(ageInfo.getDateOfBirth());
    }
}