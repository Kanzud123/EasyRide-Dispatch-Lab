
package easyride;

import easyride.domain.*;
import easyride.storage.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DatabasePersistenceEdgeCaseTest {

    @Test
    void testCustomerRecordCanBeUpdated(@TempDir Path tempDir) {

        Path dbFile = tempDir.resolve("customer-update.db");

        SqliteDatabase db = new SqliteDatabase(dbFile);
        JdbcStore store = new JdbcStore(db);

        GeoPoint location = new GeoPoint(23.7925, 90.4078);

        Customer customer = new Customer(
                "C-201",
                "Rahim",
                "01711111111",
                Money.bdt(500),
                location
        );

        store.saveCustomer(customer);

        Customer updatedCustomer = new Customer(
                "C-201",
                "Rahim Updated",
                "01711111111",
                Money.bdt(1000),
                location
        );

        store.saveCustomer(updatedCustomer);

        Optional<Customer> result =
                store.findCustomerById("C-201");

        assertTrue(result.isPresent());

        assertEquals("Rahim Updated", result.get().getName());

        assertEquals(Money.bdt(1000),
                result.get().getWalletBalance());
    }

    @Test
    void testFindNonExistingCustomerReturnsEmpty(@TempDir Path tempDir) {

        Path dbFile = tempDir.resolve("empty-customer.db");

        SqliteDatabase db = new SqliteDatabase(dbFile);
        JdbcStore store = new JdbcStore(db);

        Optional<Customer> result =
                store.findCustomerById("UNKNOWN-ID");

        assertTrue(result.isEmpty());
    }
}
