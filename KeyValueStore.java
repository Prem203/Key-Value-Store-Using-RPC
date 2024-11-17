import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KeyValueStore extends Remote {
    // Method to retrieve a value by key
    String get(String key) throws RemoteException;

    // Method to insert or update a key-value pair
    String put(String key, String value) throws RemoteException;

    // Method to delete a key-value pair
    String delete(String key) throws RemoteException;
}
