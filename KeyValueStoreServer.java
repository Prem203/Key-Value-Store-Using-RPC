import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class KeyValueStoreServer {
    public static void main(String[] args) {
        try {
            KeyValueStore keyValueStore = new KeyValueStoreImpl();
            
            // Try to create a new registry on port `1099`
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("RMI registry created on port 1099.");
            } catch (java.rmi.server.ExportException e) {
                // If the registry is already running, get the existing one
                registry = LocateRegistry.getRegistry(1099);
                System.out.println("Using existing RMI registry on port 1099.");
            }
            
            registry.rebind("KeyValueStore", keyValueStore);
            System.out.println("Server is ready.");
            
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
