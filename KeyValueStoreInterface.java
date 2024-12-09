import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KeyValueStoreInterface extends Remote {
    String get(String key) throws RemoteException;

    boolean put(String key, String value) throws RemoteException;

    boolean delete(String key) throws RemoteException;

    boolean prepare(int proposalNumber, String operation) throws RemoteException;

    boolean accept(int proposalNumber, String operation) throws RemoteException;

    void learn(String operation) throws RemoteException;
}