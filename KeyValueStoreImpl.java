import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class KeyValueStoreImpl extends UnicastRemoteObject implements KeyValueStore {
    private final Map<String, String> store;
    private final ReentrantLock lock;

    protected KeyValueStoreImpl() throws RemoteException {
        super();
        this.store = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public String get(String key) throws RemoteException {
        try {
            lock.lock();
            if (store.containsKey(key)) {
                return "Value: " + store.get(key);
            } else {
                return "Error: Key not found";
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String put(String key, String value) throws RemoteException {
        try {
            lock.lock();
            store.put(key, value);
            return "Success: Key " + key + " added/updated";
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String delete(String key) throws RemoteException {
        try {
            lock.lock();
            if (store.remove(key) != null) {
                return "Success: Key " + key + " removed";
            } else {
                return "Error: Key not found";
            }
        } finally {
            lock.unlock();
        }
    }
}
