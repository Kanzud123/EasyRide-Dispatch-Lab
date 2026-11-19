package easyride.fare;

import easyride.domain.Money;
import easyride.domain.VehicleType;

public class BaseFareCalculator implements FareCalculator {

    @Override
    public Money calculateFare(double distanceKm, VehicleType vehicleType) {
        if (distanceKm < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        double baseFare;
        double perKmRate;

        switch (vehicleType) {
            case BIKE -> {
                baseFare = 30.0;
                perKmRate = 12.0;
            }
            case CNG -> {
                baseFare = 50.0;
                perKmRate = 18.0;
            }
            case CAR -> {
                baseFare = 80.0;
                perKmRate = 25.0;
            }
            case MICRO -> {
                baseFare = 120.0;
                perKmRate = 35.0;
            }
            default -> throw new IllegalArgumentException("Unsupported vehicle type: " + vehicleType);
        }

        double total = baseFare + (distanceKm * perKmRate);
        return Money.bdt(total);
    }
}
