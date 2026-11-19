package easyride.matching;

import easyride.domain.Driver;
import easyride.domain.GeoPoint;
import easyride.domain.VehicleType;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class NearestDriverMatcher implements DriverMatcher {

    @Override
    public Optional<Driver> findBestDriver(List<Driver> drivers, GeoPoint pickupLocation, VehicleType requestedType) {
        return drivers.stream()
                .filter(Driver::isAvailable)
                .filter(d -> d.getVehicleType() == requestedType)
                .min(Comparator.comparingDouble(d -> d.getCurrentLocation().distanceKmTo(pickupLocation)));
    }
}
