import java.rmi.*;
import java.rmi.registry.*;

public class KeyValueStoreClient {
    private final KeyValueStoreInterface server;

    public KeyValueStoreClient(String host, int port) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, port);
        this.server = (KeyValueStoreInterface) registry.lookup("KeyValueStore");
    }

    public void testOperations() throws RemoteException {
    String[][] data = {
        {"Name", "Alice"},
        {"Age", "30"},
        {"City", "New York"},
        {"Course", "Distributed Systems"},
        {"Hobby", "Cycling"},
    };

    System.out.println("Performing 5 PUT operations...");
    for (String[] entry : data) {
        try {
            boolean putResult = server.put(entry[0], entry[1]);
            System.out.println("PUT " + entry[0] + ": " + entry[1] + " -> " + (putResult ? "Succeeded" : "Failed"));
        } catch (RemoteException e) {
            System.err.println("PUT operation failed for key: " + entry[0] + ". Error: " + e.getMessage());
        }
    }

    System.out.println("\nPerforming 5 GET operations...");
    for (String[] entry : data) {
        try {
            String value = server.get(entry[0]);
            System.out.println("GET " + entry[0] + ": " + (value != null ? value : "Key not found"));
        } catch (RemoteException e) {
            System.err.println("GET operation failed for key: " + entry[0] + ". Error: " + e.getMessage());
        }
    }

    System.out.println("\nPerforming 5 DELETE operations...");
    for (String[] entry : data) {
        try {
            boolean deleteResult = server.delete(entry[0]);
            System.out.println("DELETE " + entry[0] + ": " + (deleteResult ? "Succeeded" : "Failed"));
        } catch (RemoteException e) {
            System.err.println("DELETE operation failed for key: " + entry[0] + ". Error: " + e.getMessage());
        }
    }

    System.out.println("\nAll operations completed.");
}


    public static void main(String[] args) {
    try {
        String host = System.getenv("SERVER_HOST");
        int port = Integer.parseInt(System.getenv("SERVER_PORT"));

        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("Environment variable SERVER_HOST is missing or empty.");
        }

        KeyValueStoreClient client = new KeyValueStoreClient(host, port);
        System.out.println("Connected to server at " + host + ":" + port);
        client.testOperations();
    } catch (IllegalArgumentException e) {
        System.err.println("Configuration Error: " + e.getMessage());
    } catch (Exception e) {
        System.err.println("Client encountered an error: " + e.getMessage());
        e.printStackTrace();
    }
    }

}