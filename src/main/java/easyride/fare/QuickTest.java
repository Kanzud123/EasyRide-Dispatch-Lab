package easyride.fare;

import easyride.domain.*;
import java.time.Instant;

public class QuickTest {
    public static void main(String[] args) {
        // ১. দুটি লোকেশন ও দূরত্ব তৈরি (গুলশান থেকে ধানমন্ডি)
        GeoPoint gulshan = new GeoPoint(23.7925, 90.4078);
        GeoPoint dhanmondi = new GeoPoint(23.7461, 90.3742);
        double distance = gulshan.distanceKmTo(dhanmondi);

        // ২. ভাড়া ক্যালকুলেটর টেস্ট (CAR + 1.5x Surge + 30 BDT Discount)
        FareCalculator baseCalc = new BaseFareCalculator();
        FareCalculator surgeCalc = new SurgeFareDecorator(baseCalc, 1.5);
        FareCalculator finalCalc = new DiscountFareDecorator(surgeCalc, Money.bdt(30));

        Money baseFare = baseCalc.calculateFare(distance, VehicleType.CAR);
        Money finalFare = finalCalc.calculateFare(distance, VehicleType.CAR);

        System.out.println("Distance: " + String.format("%.2f", distance) + " km");
        System.out.println("Base Fare (CAR): " + baseFare);
        System.out.println("Final Fare (with 1.5x Surge & 30 BDT Discount): " + finalFare);

        // ৩. ট্রিপ লাইফসাইকেল (Trip Lifecycle) টেস্ট
        Trip trip = new Trip("T-101", "C-1", "Gulshan", gulshan, "Dhanmondi", dhanmondi,
                VehicleType.CAR, distance, finalFare, Instant.now());
        System.out.println("\nTrip Created -> Status: " + trip.getStatus());

        trip.assignDriver("D-1", Instant.now());
        System.out.println("Driver Assigned -> Status: " + trip.getStatus() + ", Driver: " + trip.getDriverId());

        trip.startTrip(Instant.now());
        System.out.println("Trip Started -> Status: " + trip.getStatus());

        trip.completeTrip(Instant.now());
        trip.rateTrip(4.9);
        System.out.println("Trip Completed -> Status: " + trip.getStatus() + ", Rating: " + trip.getRating());
    }
}
