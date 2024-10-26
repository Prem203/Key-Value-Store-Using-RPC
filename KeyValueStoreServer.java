import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class KeyValueStoreServer extends UnicastRemoteObject implements KeyValueStore {
    private static final LoggerUtility logger = new LoggerUtility();
    private final Map<String, String> store;
    private final ReentrantLock lock;

    protected KeyValueStoreServer() throws RemoteException {
        super();
        this.store = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public String get(String key) throws RemoteException {
        logger.info("Received GET request for key: " + key);
        try {
            lock.lock();
            if (store.containsKey(key)) {
                String value = store.get(key);
                logger.info("GET success for key: " + key + " -> Value: " + value);
                return "Value: " + value;
            } else {
                logger.info("GET failed for key: " + key + " - Key not found");
                return "Error: Key not found";
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String put(String key, String value) throws RemoteException {
        logger.info("Received PUT request for key: " + key + " with value: " + value);
        try {
            lock.lock();
            store.put(key, value);
            logger.info("PUT success for key: " + key + " with value: " + value);
            return "Success: Key " + key + " added/updated";
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String delete(String key) throws RemoteException {
        logger.info("Received DELETE request for key: " + key);
        try {
            lock.lock();
            if (store.remove(key) != null) {
                logger.info("DELETE success for key: " + key);
                return "Success: Key " + key + " removed";
            } else {
                logger.info("DELETE failed for key: " + key + " - Key not found");
                return "Error: Key not found";
            }
        } finally {
            lock.unlock();
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

            registry.rebind("KeyValueStore", keyValueStore);
            logger.info("Server is ready.");

        } catch (Exception e) {
            logger.info("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
