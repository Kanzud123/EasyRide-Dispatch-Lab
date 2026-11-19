package easyride.fare;

import easyride.domain.Money;
import easyride.domain.VehicleType;
import java.util.Objects;

public class DiscountFareDecorator extends FareDecorator {
    private final Money discountAmount;

    public DiscountFareDecorator(FareCalculator wrapped, Money discountAmount) {
        super(wrapped);
        this.discountAmount = Objects.requireNonNull(discountAmount, "Discount amount cannot be null");
        if (discountAmount.isNegative()) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }
    }

    @Override
    public Money calculateFare(double distanceKm, VehicleType vehicleType) {
        Money currentFare = super.calculateFare(distanceKm, vehicleType);
        if (!currentFare.isGreaterThanOrEqual(discountAmount)) {
            return Money.zeroBdt();
        }
        return currentFare.subtract(discountAmount);
    }
}
