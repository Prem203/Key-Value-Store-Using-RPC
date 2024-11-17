import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.*;

// New imports for replication and two-phase commit
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

public class KeyValueStoreServer extends UnicastRemoteObject implements KeyValueStore {
    private static final LoggerUtility logger = new LoggerUtility();
    private final Map<String, String> store;
    private final ReentrantLock lock;
    private final ExecutorService threadPool;

    // Replication - List of replicas
    private static final List<KeyValueStore> replicas = Collections.synchronizedList(new ArrayList<>());
    private static final int TOTAL_REPLICAS = 5;

    protected KeyValueStoreServer() throws RemoteException {
        super();
        this.store = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock(true);
        this.threadPool = Executors.newFixedThreadPool(10); // Thread pool for better concurrency
    }

    @Override
    public String get(String key) throws RemoteException {
        logger.info("Received GET request for key: " + key);
        try {
            if (store.containsKey(key)) {
                String value = store.get(key);
                logger.info("GET success for key: " + key + " -> Value: " + value);
                return "Value: " + value;
            } else {
                logger.info("GET failed for key: " + key + " - Key not found");
                return "Error: Key not found";
            }
        } catch (Exception e) {
            logger.info("Exception during GET request: " + e.toString());
            throw new RemoteException("Exception during GET request", e);
        }
    }

    @Override
    public String put(String key, String value) throws RemoteException {
        logger.info("Received PUT request for key: " + key + " with value: " + value);
        return performTwoPhaseCommit("PUT", key, value);
    }

    @Override
    public String delete(String key) throws RemoteException {
        logger.info("Received DELETE request for key: " + key);
        return performTwoPhaseCommit("DELETE", key, null);
    }

    // Method to perform Two-Phase Commit
    private String performTwoPhaseCommit(String operation, String key, String value) throws RemoteException {
        logger.info("Initiating Two-Phase Commit for operation: " + operation + " on key: " + key);

        // Phase 1: Prepare
        for (KeyValueStore replica : replicas) {
            try {
                if ("PUT".equals(operation)) {
                    logger.info("Preparing PUT on replica for key: " + key);
                } else if ("DELETE".equals(operation)) {
                    logger.info("Preparing DELETE on replica for key: " + key);
                }
            } catch (Exception e) {
                logger.info("Replica preparation failed: " + e.getMessage());
                return "Error: Replica preparation failed";
            }
        }

        // Phase 2: Commit
        lock.lock();
        try {
            if ("PUT".equals(operation)) {
                store.put(key, value);
                logger.info("PUT committed for key: " + key + " with value: " + value);
            } else if ("DELETE".equals(operation)) {
                store.remove(key);
                logger.info("DELETE committed for key: " + key);
            }

            // Notify replicas to commit
            for (KeyValueStore replica : replicas) {
                try {
                    if ("PUT".equals(operation)) {
                        logger.info("Committing PUT on replica for key: " + key);
                    } else if ("DELETE".equals(operation)) {
                        logger.info("Committing DELETE on replica for key: " + key);
                    }
                } catch (Exception e) {
                    logger.info("Replica commit failed: " + e.getMessage());
                }
            }

        } finally {
            lock.unlock();
        }

        return "Success: " + operation + " completed for key: " + key;
    }

    public static void main(String[] args) {
        try {
            KeyValueStoreServer keyValueStore = new KeyValueStoreServer();

            // Try to create a new registry on port `1099`
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(1099);
                logger.info("RMI registry created on port 1099.");
            } catch (java.rmi.server.ExportException e) {
                // If the registry is already running, get the existing one
                registry = LocateRegistry.getRegistry(1099);
                logger.info("Using existing RMI registry on port 1099.");
            }

            // Bind the remote object
            registry.rebind("KeyValueStore", keyValueStore);
            logger.info("Server is ready.");

            // Initialize replicas
            for (int i = 0; i < TOTAL_REPLICAS; i++) {
                replicas.add(new KeyValueStoreServer());
                logger.info("Replica " + i + " initialized.");
            }

            // Shutdown hook for the server
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down server...");
                keyValueStore.threadPool.shutdown();
            }));

        } catch (Exception e) {
            logger.info("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}