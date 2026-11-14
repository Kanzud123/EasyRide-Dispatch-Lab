/*extends class,child*/
package easyride.domain;

public class Customer extends User {
    private GeoPoint homeLocation;

    public Customer(String id, String name, String phone, Money walletBalance, GeoPoint homeLocation) {
        super(id, name, phone, UserRole.CUSTOMER, walletBalance);
        this.homeLocation = homeLocation;
    }

    public GeoPoint getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(GeoPoint homeLocation) {
        this.homeLocation = homeLocation;
    }
}
