package easyride.matching;

import easyride.domain.Driver;
import easyride.domain.GeoPoint;
import easyride.domain.VehicleType;
import java.util.List;
import java.util.Optional;

public interface DriverMatcher {
    Optional<Driver> findBestDriver(List<Driver> drivers, GeoPoint pickupLocation, VehicleType requestedType);
}
