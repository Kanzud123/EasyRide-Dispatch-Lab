package easyride.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteDatabase {
    private final String jdbcUrl;

    public SqliteDatabase(Path dbFilePath) {
        try {
            if (dbFilePath.getParent() != null) {
                Files.createDirectories(dbFilePath.getParent());
            }
            this.jdbcUrl = "jdbc:sqlite:" + dbFilePath.toAbsolutePath();
            initializeSchema();
        } catch (Exception e) {
            throw new StorageFailure("Failed to initialize SQLite database at " + dbFilePath, e);
        }
    }

    public Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    private void initializeSchema() {
        String customersTable = """
                CREATE TABLE IF NOT EXISTS customers (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    wallet_balance REAL NOT NULL,
                    home_lat REAL,
                    home_lon REAL
                );
                """;

        String driversTable = """
                CREATE TABLE IF NOT EXISTS drivers (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    wallet_balance REAL NOT NULL,
                    vehicle_type TEXT NOT NULL,
                    plate_number TEXT NOT NULL,
                    current_lat REAL NOT NULL,
                    current_lon REAL NOT NULL,
                    available INTEGER NOT NULL,
                    rating REAL NOT NULL,
                    completed_trips INTEGER NOT NULL
                );
                """;

        String tripsTable = """
                CREATE TABLE IF NOT EXISTS trips (
                    id TEXT PRIMARY KEY,
                    customer_id TEXT NOT NULL,
                    driver_id TEXT,
                    pickup_label TEXT NOT NULL,
                    pickup_lat REAL NOT NULL,
                    pickup_lon REAL NOT NULL,
                    dropoff_label TEXT NOT NULL,
                    dropoff_lon REAL NOT NULL,
                    dropoff_lat REAL NOT NULL,
                    vehicle_type TEXT NOT NULL,
                    distance_km REAL NOT NULL,
                    fare_amount REAL NOT NULL,
                    status TEXT NOT NULL,
                    requested_at TEXT NOT NULL,
                    accepted_at TEXT,
                    started_at TEXT,
                    completed_at TEXT,
                    cancelled_at TEXT,
                    cancellation_reason TEXT,
                    rating REAL,
                    FOREIGN KEY (customer_id) REFERENCES customers(id)
                );
                """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(customersTable);
            stmt.execute(driversTable);
            stmt.execute(tripsTable);
        } catch (SQLException e) {
            throw new StorageFailure("Failed to create database tables", e);
        }
    }
}
