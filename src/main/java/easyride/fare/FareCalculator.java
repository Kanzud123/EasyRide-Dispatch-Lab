package easyride.fare;

import easyride.domain.Money;
import easyride.domain.VehicleType;

public interface FareCalculator {
    Money calculateFare(double distanceKm, VehicleType vehicleType);
}
