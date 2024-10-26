import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class KeyValueStoreClient {
    private static final LoggerUtility logger = new LoggerUtility();

    public static void main(String[] args) {
        try {
            // Connect to the server registry
            Registry registry = LocateRegistry.getRegistry("kv_server", 1099);
            KeyValueStore keyValueStore = (KeyValueStore) registry.lookup("KeyValueStore");

            // Pre-populate the store with initial key-value pairs
            prePopulateStore(keyValueStore);

            // Perform at least 5 PUT operations
            logger.info("Performing 5 PUT operations...");
            logger.info(keyValueStore.put("hobby", "Cycling"));
            logger.info(keyValueStore.put("language", "Java"));
            logger.info(keyValueStore.put("course", "Distributed Systems"));
            logger.info(keyValueStore.put("os", "Linux"));
            logger.info(keyValueStore.put("city", "San Francisco"));

            // Perform at least 5 GET operations
            logger.info("\nPerforming 5 GET operations...");
            logger.info("Value: " + keyValueStore.get("hobby"));
            logger.info("Value: " + keyValueStore.get("language"));
            logger.info("Value: " + keyValueStore.get("course"));
            logger.info("Value: " + keyValueStore.get("os"));
            logger.info("Value: " + keyValueStore.get("city"));

            // Perform at least 5 DELETE operations
            logger.info("\nPerforming 5 DELETE operations...");
            logger.info(keyValueStore.delete("hobby"));
            logger.info(keyValueStore.delete("language"));
            logger.info(keyValueStore.delete("course"));
            logger.info(keyValueStore.delete("os"));
            logger.info(keyValueStore.delete("city"));

            logger.info("\nAll operations completed successfully.");

        } catch (Exception e) {
            logger.info("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void prePopulateStore(KeyValueStore keyValueStore) {
        try {
            logger.info("Pre-populating the key-value store with initial data...");
            logger.info(keyValueStore.put("name", "Alice"));
            logger.info(keyValueStore.put("age", "30"));
            logger.info(keyValueStore.put("email", "alice@example.com"));
            logger.info(keyValueStore.put("country", "USA"));
            logger.info(keyValueStore.put("city", "New York"));
            logger.info("Pre-population completed.\n");
        } catch (Exception e) {
            logger.info("Exception during pre-population: " + e.toString());
            e.printStackTrace();
        }
    }
}
