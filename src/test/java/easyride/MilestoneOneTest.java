package easyride;

import easyride.domain.*;
import easyride.fare.*;
import easyride.matching.*;
import easyride.storage.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MilestoneOneTest {

    @Test
    void testGeoPointDistanceCalculation() {
        GeoPoint gulshan = new GeoPoint(23.7925, 90.4078);
        GeoPoint dhanmondi = new GeoPoint(23.7461, 90.3742);
        double km = gulshan.distanceKmTo(dhanmondi);
        assertEquals(6.18, km, 0.1, "Gulshan to Dhanmondi should be ~6.18 km");
    }

    @Test
    void testFareDecoratorsWithSurgeAndDiscount() {
        FareCalculator base = new BaseFareCalculator();
        FareCalculator surge = new SurgeFareDecorator(base, 1.5);
        FareCalculator discounted = new DiscountFareDecorator(surge, Money.bdt(50));

        Money fare = discounted.calculateFare(10.0, VehicleType.CAR);
        // Base for CAR: 80 + (10 * 25) = 330 -> 1.5x = 495 -> -50 = 445.00 BDT
        assertEquals(Money.bdt(445.0), fare);
    }

    @Test
    void testNearestAndHighestRatedDriverMatching() {
        GeoPoint pickup = new GeoPoint(23.7925, 90.4078); // Gulshan
        Driver nearDriver = new Driver("D1", "Rahim", "01711", Money.zeroBdt(), VehicleType.CAR,
                "DHA-11", new GeoPoint(23.7940, 90.4100), true, 4.5, 10);
        Driver topRatedDriver = new Driver("D2", "Karim", "01811", Money.zeroBdt(), VehicleType.CAR,
                "DHA-22", new GeoPoint(23.7461, 90.3742), true, 4.95, 50);

        List<Driver> fleet = List.of(nearDriver, topRatedDriver);

        DriverMatcher nearest = new NearestDriverMatcher();
        assertEquals("D1", nearest.findBestDriver(fleet, pickup, VehicleType.CAR).get().getId());

        DriverMatcher highestRated = new HighestRatedDriverMatcher();
        assertEquals("D2", highestRated.findBestDriver(fleet, pickup, VehicleType.CAR).get().getId());
    }

    @Test
    void testSqliteDatabasePersistence(@TempDir Path tempDir) {
        Path dbFile = tempDir.resolve("test-easyride.db");
        SqliteDatabase db = new SqliteDatabase(dbFile);
        JdbcStore store = new JdbcStore(db);

        // 1. Save & Load Customer
        GeoPoint gulshan = new GeoPoint(23.7925, 90.4078);
        GeoPoint uttara = new GeoPoint(23.8759, 90.3795);
        Customer customer = new Customer("C-101", "Tanvir", "01900000000", Money.bdt(1000), gulshan);
        store.saveCustomer(customer);

        Optional<Customer> loadedCustomer = store.findCustomerById("C-101");
        assertTrue(loadedCustomer.isPresent());
        assertEquals("Tanvir", loadedCustomer.get().getName());

        // 2. Save & Load Driver
        Driver driver = new Driver("D-201", "Kamal", "01600000000", Money.bdt(200), VehicleType.CAR,
                "DHAKA-GA-1234", gulshan, true, 4.85, 25);
        store.saveDriver(driver);
        assertEquals(1, store.findAllDrivers().size());

        // 3. Save, Complete & Reload Trip
        Trip trip = new Trip("T-501", "C-101", "Gulshan", gulshan, "Uttara", uttara,
                VehicleType.CAR, 9.7, Money.bdt(322.5), Instant.now());
        trip.assignDriver("D-201", Instant.now());
        trip.startTrip(Instant.now());
        trip.completeTrip(Instant.now());
        trip.rateTrip(5.0);
        store.saveTrip(trip);

        List<Trip> savedTrips = store.findAllTrips();
        assertEquals(1, savedTrips.size());
        assertEquals(TripStatus.COMPLETED, savedTrips.get(0).getStatus());
        assertEquals(5.0, savedTrips.get(0).getRating());
    }
}
