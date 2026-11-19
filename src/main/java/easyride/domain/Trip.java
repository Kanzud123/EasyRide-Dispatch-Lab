package easyride.domain;

import java.time.Instant;
import java.util.Objects;

public class Trip {
    private final String id;
    private final String customerId;
    private String driverId;
    private final String pickupLabel;
    private final GeoPoint pickupLocation;
    private final String dropoffLabel;
    private final GeoPoint dropoffLocation;
    private final VehicleType vehicleType;
    private final double distanceKm;
    private final Money fare;
    private TripStatus status;
    private final Instant requestedAt;
    private Instant acceptedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant cancelledAt;
    private String cancellationReason;
    private Double rating;

    public Trip(String id,
                String customerId,
                String pickupLabel,
                GeoPoint pickupLocation,
                String dropoffLabel,
                GeoPoint dropoffLocation,
                VehicleType vehicleType,
                double distanceKm,
                Money fare,
                Instant requestedAt) {
        this.id = Objects.requireNonNull(id, "Trip ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.pickupLabel = Objects.requireNonNull(pickupLabel, "Pickup label cannot be null");
        this.pickupLocation = Objects.requireNonNull(pickupLocation, "Pickup location cannot be null");
        this.dropoffLabel = Objects.requireNonNull(dropoffLabel, "Dropoff label cannot be null");
        this.dropoffLocation = Objects.requireNonNull(dropoffLocation, "Dropoff location cannot be null");
        this.vehicleType = Objects.requireNonNull(vehicleType, "Vehicle type cannot be null");
        if (distanceKm < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        this.distanceKm = distanceKm;
        this.fare = Objects.requireNonNull(fare, "Fare cannot be null");
        this.status = TripStatus.REQUESTED;
        this.requestedAt = requestedAt != null ? requestedAt : Instant.now();
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getDriverId() { return driverId; }
    public String getPickupLabel() { return pickupLabel; }
    public GeoPoint getPickupLocation() { return pickupLocation; }
    public String getDropoffLabel() { return dropoffLabel; }
    public GeoPoint getDropoffLocation() { return dropoffLocation; }
    public VehicleType getVehicleType() { return vehicleType; }
    public double getDistanceKm() { return distanceKm; }
    public Money getFare() { return fare; }
    public TripStatus getStatus() { return status; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getAcceptedAt() { return acceptedAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public Instant getCancelledAt() { return cancelledAt; }
    public String getCancellationReason() { return cancellationReason; }
    public Double getRating() { return rating; }

    public void assignDriver(String driverId, Instant when) {
        if (this.status != TripStatus.REQUESTED) {
            throw new IllegalStateException("Trip can only be accepted from REQUESTED state, current: " + this.status);
        }
        this.driverId = Objects.requireNonNull(driverId, "Driver ID cannot be null");
        this.status = TripStatus.ACCEPTED;
        this.acceptedAt = when != null ? when : Instant.now();
    }

    public void startTrip(Instant when) {
        if (this.status != TripStatus.ACCEPTED) {
            throw new IllegalStateException("Trip can only be started from ACCEPTED state, current: " + this.status);
        }
        this.status = TripStatus.IN_PROGRESS;
        this.startedAt = when != null ? when : Instant.now();
    }

    public void completeTrip(Instant when) {
        if (this.status != TripStatus.IN_PROGRESS) {
            throw new IllegalStateException("Trip can only be completed from IN_PROGRESS state, current: " + this.status);
        }
        this.status = TripStatus.COMPLETED;
        this.completedAt = when != null ? when : Instant.now();
    }

    public void cancelTrip(String reason, Instant when) {
        if (this.status == TripStatus.COMPLETED || this.status == TripStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a trip that is already " + this.status);
        }
        this.status = TripStatus.CANCELLED;
        this.cancellationReason = (reason == null || reason.isBlank()) ? "No reason provided" : reason;
        this.cancelledAt = when != null ? when : Instant.now();
    }

    public void rateTrip(double rating) {
        if (this.status != TripStatus.COMPLETED) {
            throw new IllegalStateException("Only completed trips can be rated");
        }
        if (rating < 1.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }
        this.rating = rating;
    }

    // ডেটাবেস থেকে পুরনো ট্রিপ লোড করার জন্য স্টেট রিস্টোর মেথড
    public void restoreState(String driverId,
                             TripStatus status,
                             Instant acceptedAt,
                             Instant startedAt,
                             Instant completedAt,
                             Instant cancelledAt,
                             String cancellationReason,
                             Double rating) {
        this.driverId = driverId;
        this.status = Objects.requireNonNull(status);
        this.acceptedAt = acceptedAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.cancelledAt = cancelledAt;
        this.cancellationReason = cancellationReason;
        this.rating = rating;
    }
}