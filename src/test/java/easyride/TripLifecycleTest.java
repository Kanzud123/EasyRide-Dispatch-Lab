package easyride;

import easyride.domain.*;
        import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TripLifecycleTest {

    private Trip createTrip() {
        GeoPoint gulshan = new GeoPoint(23.7925, 90.4078);
        GeoPoint uttara = new GeoPoint(23.8759, 90.3795);

        return new Trip(
                "T-101",
                "C-101",
                "Gulshan",
                gulshan,
                "Uttara",
                uttara,
                VehicleType.CAR,
                9.7,
                Money.bdt(322.5),
                Instant.now()
        );
    }

    @Test
    void testSuccessfulTripLifecycle() {
        Trip trip = createTrip();

        assertEquals(TripStatus.REQUESTED, trip.getStatus());

        trip.assignDriver("D-101", Instant.now());
        assertEquals(TripStatus.ACCEPTED, trip.getStatus());

        trip.startTrip(Instant.now());
        assertEquals(TripStatus.IN_PROGRESS, trip.getStatus());

        trip.completeTrip(Instant.now());
        assertEquals(TripStatus.COMPLETED, trip.getStatus());

        trip.rateTrip(5.0);
        assertEquals(5.0, trip.getRating());
    }

    @Test
    void testCannotStartTripBeforeDriverAssignment() {
        Trip trip = createTrip();

        assertThrows(IllegalStateException.class,
                () -> trip.startTrip(Instant.now()));
    }

    @Test
    void testCannotCompleteTripBeforeStarting() {
        Trip trip = createTrip();

        trip.assignDriver("D-101", Instant.now());

        assertThrows(IllegalStateException.class,
                () -> trip.completeTrip(Instant.now()));
    }

    @Test
    void testCannotRateIncompleteTrip() {
        Trip trip = createTrip();

        assertThrows(IllegalStateException.class,
                () -> trip.rateTrip(5.0));
    }

    @Test
    void testCannotCancelCompletedTrip() {
        Trip trip = createTrip();

        trip.assignDriver("D-101", Instant.now());
        trip.startTrip(Instant.now());
        trip.completeTrip(Instant.now());

        assertThrows(IllegalStateException.class,
                () -> trip.cancelTrip("Too late", Instant.now()));
    }
}
