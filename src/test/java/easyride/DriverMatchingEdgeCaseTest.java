
package easyride;

import easyride.domain.*;
import easyride.matching.*;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DriverMatchingEdgeCaseTest {

    @Test
    void unavailableDriverShouldNotBeSelected() {

        GeoPoint pickup = new GeoPoint(23.7925, 90.4078);

        Driver unavailableDriver = new Driver(
                "D1", "Rahim", "01711",
                Money.zeroBdt(),
                VehicleType.CAR,
                "DHA-11",
                pickup,
                false,
                4.9,
                10
        );

        List<Driver> fleet = List.of(unavailableDriver);

        DriverMatcher nearest = new NearestDriverMatcher();
        DriverMatcher highestRated = new HighestRatedDriverMatcher();

        assertTrue(nearest.findBestDriver(
                fleet, pickup, VehicleType.CAR).isEmpty());

        assertTrue(highestRated.findBestDriver(
                fleet, pickup, VehicleType.CAR).isEmpty());
    }

    @Test
    void wrongVehicleTypeShouldNotBeSelected() {

        GeoPoint pickup = new GeoPoint(23.7925, 90.4078);

        Driver bikeDriver = new Driver(
                "D2", "Karim", "01811",
                Money.zeroBdt(),
                VehicleType.BIKE,
                "DHA-22",
                pickup,
                true,
                4.8,
                20
        );

        List<Driver> fleet = List.of(bikeDriver);

        DriverMatcher nearest = new NearestDriverMatcher();

        assertTrue(nearest.findBestDriver(
                fleet, pickup, VehicleType.CAR).isEmpty());
    }

    @Test
    void emptyFleetShouldReturnEmptyResult() {

        GeoPoint pickup = new GeoPoint(23.7925, 90.4078);

        List<Driver> fleet = List.of();

        DriverMatcher nearest = new NearestDriverMatcher();
        DriverMatcher highestRated = new HighestRatedDriverMatcher();

        assertTrue(nearest.findBestDriver(
                fleet, pickup, VehicleType.CAR).isEmpty());

        assertTrue(highestRated.findBestDriver(
                fleet, pickup, VehicleType.CAR).isEmpty());
    }
}