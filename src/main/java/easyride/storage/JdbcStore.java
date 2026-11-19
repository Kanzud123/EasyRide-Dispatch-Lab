package easyride.storage;

import easyride.domain.*;
import easyride.domain.Driver;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStore {
    private final SqliteDatabase db;

    public JdbcStore(SqliteDatabase db) {
        this.db = db;
    }

    // ==================== CUSTOMER OPERATIONS ====================

    public void saveCustomer(Customer c) {
        String sql = """
                INSERT INTO customers (id, name, phone, wallet_balance, home_lat, home_lon)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    name = excluded.name,
                    phone = excluded.phone,
                    wallet_balance = excluded.wallet_balance,
                    home_lat = excluded.home_lat,
                    home_lon = excluded.home_lon;
                """;
        try (Connection conn = db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getPhone());
            ps.setDouble(4, c.getWalletBalance().amount().doubleValue());
            if (c.getHomeLocation() != null) {
                ps.setDouble(5, c.getHomeLocation().lat());
                ps.setDouble(6, c.getHomeLocation().lon());
            } else {
                ps.setNull(5, Types.REAL);
                ps.setNull(6, Types.REAL);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new StorageFailure("Failed to save customer " + c.getId(), e);
        }
    }

    public Optional<Customer> findCustomerById(String id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GeoPoint home = null;
                    double lat = rs.getDouble("home_lat");
                    if (!rs.wasNull()) {
                        home = new GeoPoint(lat, rs.getDouble("home_lon"));
                    }
                    return Optional.of(new Customer(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            Money.bdt(rs.getDouble("wallet_balance")),
                            home
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new StorageFailure("Failed to find customer " + id, e);
        }
    }

    // ==================== DRIVER OPERATIONS ====================

    public void saveDriver(Driver d) {
        String sql = """
                INSERT INTO drivers (id, name, phone, wallet_balance, vehicle_type, plate_number,
                                     current_lat, current_lon, available, rating, completed_trips)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    name = excluded.name,
                    phone = excluded.phone,
                    wallet_balance = excluded.wallet_balance,
                    current_lat = excluded.current_lat,
                    current_lon = excluded.current_lon,
                    available = excluded.available,
                    rating = excluded.rating,
                    completed_trips = excluded.completed_trips;
                """;
        try (Connection conn = db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getId());
            ps.setString(2, d.getName());
            ps.setString(3, d.getPhone());
            ps.setDouble(4, d.getWalletBalance().amount().doubleValue());
            ps.setString(5, d.getVehicleType().name());
            ps.setString(6, d.getPlateNumber());
            ps.setDouble(7, d.getCurrentLocation().lat());
            ps.setDouble(8, d.getCurrentLocation().lon());
            ps.setInt(9, d.isAvailable() ? 1 : 0);
            ps.setDouble(10, d.getRating());
            ps.setInt(11, d.getCompletedTrips());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new StorageFailure("Failed to save driver " + d.getId(), e);
        }
    }

    public List<Driver> findAllDrivers() {
        String sql = "SELECT * FROM drivers";
        List<Driver> drivers = new ArrayList<>();
        try (Connection conn = db.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                drivers.add(mapDriver(rs));
            }
            return drivers;
        } catch (SQLException e) {
            throw new StorageFailure("Failed to load drivers", e);
        }
    }

    private Driver mapDriver(ResultSet rs) throws SQLException {
        return new Driver(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("phone"),
                Money.bdt(rs.getDouble("wallet_balance")),
                VehicleType.valueOf(rs.getString("vehicle_type")),
                rs.getString("plate_number"),
                new GeoPoint(rs.getDouble("current_lat"), rs.getDouble("current_lon")),
                rs.getInt("available") == 1,
                rs.getDouble("rating"),
                rs.getInt("completed_trips")
        );
    }

    // ==================== TRIP OPERATIONS ====================

    public void saveTrip(Trip t) {
        String sql = """
                INSERT INTO trips (id, customer_id, driver_id, pickup_label, pickup_lat, pickup_lon,
                                   dropoff_label, dropoff_lat, dropoff_lon, vehicle_type, distance_km,
                                   fare_amount, status, requested_at, accepted_at, started_at,
                                   completed_at, cancelled_at, cancellation_reason, rating)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    driver_id = excluded.driver_id,
                    status = excluded.status,
                    accepted_at = excluded.accepted_at,
                    started_at = excluded.started_at,
                    completed_at = excluded.completed_at,
                    cancelled_at = excluded.cancelled_at,
                    cancellation_reason = excluded.cancellation_reason,
                    rating = excluded.rating;
                """;
        try (Connection conn = db.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getId());
            ps.setString(2, t.getCustomerId());
            ps.setString(3, t.getDriverId());
            ps.setString(4, t.getPickupLabel());
            ps.setDouble(5, t.getPickupLocation().lat());
            ps.setDouble(6, t.getPickupLocation().lon());
            ps.setString(7, t.getDropoffLabel());
            ps.setDouble(8, t.getDropoffLocation().lat());
            ps.setDouble(9, t.getDropoffLocation().lon());
            ps.setString(10, t.getVehicleType().name());
            ps.setDouble(11, t.getDistanceKm());
            ps.setDouble(12, t.getFare().amount().doubleValue());
            ps.setString(13, t.getStatus().name());
            ps.setString(14, t.getRequestedAt().toString());
            ps.setString(15, t.getAcceptedAt() != null ? t.getAcceptedAt().toString() : null);
            ps.setString(16, t.getStartedAt() != null ? t.getStartedAt().toString() : null);
            ps.setString(17, t.getCompletedAt() != null ? t.getCompletedAt().toString() : null);
            ps.setString(18, t.getCancelledAt() != null ? t.getCancelledAt().toString() : null);
            ps.setString(19, t.getCancellationReason());
            if (t.getRating() != null) {
                ps.setDouble(20, t.getRating());
            } else {
                ps.setNull(20, Types.REAL);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new StorageFailure("Failed to save trip " + t.getId(), e);
        }
    }

    public List<Trip> findAllTrips() {
        String sql = "SELECT * FROM trips ORDER BY requested_at DESC";
        List<Trip> trips = new ArrayList<>();
        try (Connection conn = db.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                trips.add(mapTrip(rs));
            }
            return trips;
        } catch (SQLException e) {
            throw new StorageFailure("Failed to load trips", e);
        }
    }

    private Trip mapTrip(ResultSet rs) throws SQLException {
        Trip trip = new Trip(
                rs.getString("id"),
                rs.getString("customer_id"),
                rs.getString("pickup_label"),
                new GeoPoint(rs.getDouble("pickup_lat"), rs.getDouble("pickup_lon")),
                rs.getString("dropoff_label"),
                new GeoPoint(rs.getDouble("dropoff_lat"), rs.getDouble("dropoff_lon")),
                VehicleType.valueOf(rs.getString("vehicle_type")),
                rs.getDouble("distance_km"),
                Money.bdt(rs.getDouble("fare_amount")),
                Instant.parse(rs.getString("requested_at"))
        );

        String acc = rs.getString("accepted_at");
        String sta = rs.getString("started_at");
        String com = rs.getString("completed_at");
        String can = rs.getString("cancelled_at");
        double rat = rs.getDouble("rating");
        Double ratingObj = rs.wasNull() ? null : rat;

        trip.restoreState(
                rs.getString("driver_id"),
                TripStatus.valueOf(rs.getString("status")),
                acc != null ? Instant.parse(acc) : null,
                sta != null ? Instant.parse(sta) : null,
                com != null ? Instant.parse(com) : null,
                can != null ? Instant.parse(can) : null,
                rs.getString("cancellation_reason"),
                ratingObj
        );
        return trip;
    }
}