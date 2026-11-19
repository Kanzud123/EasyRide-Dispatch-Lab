package easyride.fare;

import easyride.domain.Money;
import easyride.domain.VehicleType;

public class SurgeFareDecorator extends FareDecorator {
    private final double multiplier;

    public SurgeFareDecorator(FareCalculator wrapped, double multiplier) {
        super(wrapped);
        if (multiplier < 1.0) {
            throw new IllegalArgumentException("Surge multiplier must be at least 1.0");
        }
        this.multiplier = multiplier;
    }

    @Override
    public Money calculateFare(double distanceKm, VehicleType vehicleType) {
        Money base = super.calculateFare(distanceKm, vehicleType);
        return base.multiply(multiplier);
    }
}
