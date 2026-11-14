package easyride;

import easyride.domain.*;

public class QuickTest {
    public static void main(String[] args) {
        // ১. দুটি লোকেশন তৈরি (গুলশান ও ধানমন্ডি)
        GeoPoint gulshan = new GeoPoint(23.7925, 90.4078);
        GeoPoint dhanmondi = new GeoPoint(23.7461, 90.3742);
        System.out.println("Distance: " + String.format("%.2f", gulshan.distanceKmTo(dhanmondi)) + " km");

        // ২. কাস্টমার ও তার ওয়ালেট চেক
        Customer customer = new Customer("C1", "Rahim", "01700000000", Money.bdt(500), gulshan);
        customer.debitWallet(Money.bdt(150));
        System.out.println("Customer Remaining Balance: " + customer.getWalletBalance());

        // ৩. ড্রাইভার এবং ট্রিপ শেষে লোকেশন আপডেট চেক
        Driver driver = new Driver("D1", "Kanzud", "01800000000", Money.zeroBdt(), VehicleType.CAR, "DHAKA-11-2233", gulshan, true, 5.0, 0);
        driver.recordTripCompletion(4.8, dhanmondi);
        System.out.println("Driver New Location: " + driver.getCurrentLocation());
        System.out.println("Driver New Rating: " + driver.getRating());
    }
}
