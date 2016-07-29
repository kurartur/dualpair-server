package lt.dualpair.server.domain.model.geo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DistanceCalculatorTest {

    private DistanceCalculator distanceCalculator = new DistanceCalculator();

    @Test
    public void testCalculate() throws Exception {
        assertEquals(311.6, distanceCalculator.calculate(10, 10, 12, 12)/1000, 0.023);
    }
}