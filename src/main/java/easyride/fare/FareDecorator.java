package easyride.fare;

import easyride.domain.Money;
import easyride.domain.VehicleType;
import java.util.Objects;

public abstract class FareDecorator implements FareCalculator {
    protected final FareCalculator wrapped;

    protected FareDecorator(FareCalculator wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped, "Wrapped calculator cannot be null");
    }

    @Override
    public Money calculateFare(double distanceKm, VehicleType vehicleType) {
        return wrapped.calculateFare(distanceKm, vehicleType);
    }
}
