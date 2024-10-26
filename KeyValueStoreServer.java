import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class KeyValueStoreServer extends UnicastRemoteObject implements KeyValueStore {
    private static final LoggerUtility logger = new LoggerUtility();
    private final Map<String, String> store;
    private final ReentrantLock lock;
    private final ExecutorService threadPool;

    protected KeyValueStoreServer() throws RemoteException {
        super();
        this.store = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock(true);
        this.threadPool = Executors.newFixedThreadPool(10);  // Create a thread pool with 10 threads
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
        try {
            lock.lock();  // Lock for write operations
            store.put(key, value);
            logger.info("PUT success for key: " + key + " with value: " + value);
            return "Success: Key " + key + " added/updated";
        }finally {
            lock.unlock();  // Unlock after the write operation
        }
    }

    @Override
    public String delete(String key) throws RemoteException {
        logger.info("Received DELETE request for key: " + key);
        try {
            lock.lock();  // Lock for delete operations
            if (store.remove(key) != null) {
                logger.info("DELETE success for key: " + key);
                return "Success: Key " + key + " removed";
            } else {
                logger.info("DELETE failed for key: " + key + " - Key not found");
                return "Error: Key not found";
            }
        } finally {
            lock.unlock();  // Unlock after the delete operation
        }
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
