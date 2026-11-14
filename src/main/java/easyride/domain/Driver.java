/*inherit korbe ,but it has its owns features,
VehicleType, plateNumber, currentLocation or GeoPoint, available, rating, completedTrips

 */
package easyride.domain;

import java.util.Objects;

public class Driver extends User {
    private final VehicleType vehicleType;
    private final String plateNumber;
    private GeoPoint currentLocation;
    private boolean available;
    private double rating;
    private int completedTrips;

    public Driver(String id,
                  String name,
                  String phone,
                  Money walletBalance,
                  VehicleType vehicleType,
                  String plateNumber,
                  GeoPoint currentLocation,
                  boolean available,
                  double rating,
                  int completedTrips) {
        super(id, name, phone, UserRole.DRIVER, walletBalance);
        this.vehicleType = Objects.requireNonNull(vehicleType, "Vehicle type cannot be null");
        this.plateNumber = Objects.requireNonNull(plateNumber, "Plate number cannot be null");
        this.currentLocation = Objects.requireNonNull(currentLocation, "Current location cannot be null");
        this.available = available;
        this.rating = rating > 0 ? rating : 5.0;
        this.completedTrips = Math.max(0, completedTrips);
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    public void updateLocation(GeoPoint newLocation) {
        this.currentLocation = Objects.requireNonNull(newLocation, "New location cannot be null");
    }//current location active hole update location kaj kore

    public boolean isAvailable() {
        return available;
    }// driver accept hole available off hoy

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getRating() {
        return rating;
    }

    public int getCompletedTrips() {
        return completedTrips;
    }

    public void recordTripCompletion(double tripRating, GeoPoint dropoffLocation) {
        if (tripRating < 1.0 || tripRating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }
        double totalScore = (this.rating * this.completedTrips) + tripRating;
        this.completedTrips++;
        this.rating = Math.round((totalScore / this.completedTrips) * 100.0) / 100.0;
        updateLocation(dropoffLocation);
        this.available = true;
    }// 1.rating,2.trip count,3.dropoff location driver er new location,4.available = true
}
