import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class KeyValueStoreClient {
    private static final LoggerUtility logger = new LoggerUtility();

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("kv_server", 1099);
            KeyValueStore keyValueStore = (KeyValueStore) registry.lookup("KeyValueStore");

            // Perform 5 PUT operations with real-world examples
            logger.info("Performing PUT operations...");
            logger.info("PUT: 'name' -> 'Alice'");
            logger.info(keyValueStore.put("name", "Alice"));

            logger.info("PUT: 'age' -> '30'");
            logger.info(keyValueStore.put("age", "30"));

            logger.info("PUT: 'city' -> 'New York'");
            logger.info(keyValueStore.put("city", "New York"));

            logger.info("PUT: 'country' -> 'USA'");
            logger.info(keyValueStore.put("country", "USA"));

            logger.info("PUT: 'email' -> 'alice@example.com'");
            logger.info(keyValueStore.put("email", "alice@example.com"));

            // Perform 5 GET operations
            logger.info("\nPerforming GET operations...");
            logger.info("GET: 'name'");
            logger.info("Value: " + keyValueStore.get("name"));

            logger.info("GET: 'age'");
            logger.info("Value: " + keyValueStore.get("age"));

            logger.info("GET: 'city'");
            logger.info("Value: " + keyValueStore.get("city"));

            logger.info("GET: 'country'");
            logger.info("Value: " + keyValueStore.get("country"));

            logger.info("GET: 'email'");
            logger.info("Value: " + keyValueStore.get("email"));

            // Perform 5 DELETE operations
            logger.info("\nPerforming DELETE operations...");
            logger.info("DELETE: 'name'");
            logger.info(keyValueStore.delete("name"));

            logger.info("DELETE: 'age'");
            logger.info(keyValueStore.delete("age"));

            logger.info("DELETE: 'city'");
            logger.info(keyValueStore.delete("city"));

            logger.info("DELETE: 'country'");
            logger.info(keyValueStore.delete("country"));

            logger.info("DELETE: 'email'");
            logger.info(keyValueStore.delete("email"));

            logger.info("\nAll operations completed successfully.");

        } catch (Exception e) {
            logger.info("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
