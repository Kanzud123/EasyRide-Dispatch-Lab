
package easyride;

import easyride.domain.*;
import easyride.fare.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FareEdgeCaseTest {

    @Test
    void testFareForAllVehicleTypes() {
        FareCalculator calculator = new BaseFareCalculator();

        assertEquals(Money.bdt(150),
                calculator.calculateFare(10, VehicleType.BIKE));

        assertEquals(Money.bdt(230),
                calculator.calculateFare(10, VehicleType.CNG));

        assertEquals(Money.bdt(330),
                calculator.calculateFare(10, VehicleType.CAR));

        assertEquals(Money.bdt(470),
                calculator.calculateFare(10, VehicleType.MICRO));
    }

    @Test
    void testZeroDistanceFare() {
        FareCalculator calculator = new BaseFareCalculator();

        assertEquals(Money.bdt(80),
                calculator.calculateFare(0, VehicleType.CAR));
    }

    @Test
    void testNegativeDistanceShouldThrowException() {
        FareCalculator calculator = new BaseFareCalculator();

        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFare(-5, VehicleType.CAR));
    }

    @Test
    void testDiscountCannotMakeFareNegative() {
        FareCalculator base = new BaseFareCalculator();

        FareCalculator discounted =
                new DiscountFareDecorator(base, Money.bdt(500));

        assertEquals(Money.zeroBdt(),
                discounted.calculateFare(10, VehicleType.BIKE));
    }

    @Test
    void testSurgeMultiplierCannotBeBelowOne() {
        FareCalculator base = new BaseFareCalculator();

        assertThrows(IllegalArgumentException.class,
                () -> new SurgeFareDecorator(base, 0.5));
    }
}
