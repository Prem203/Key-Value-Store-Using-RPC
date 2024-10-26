import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class KeyValueStoreClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("kv_server",1099);
            KeyValueStore keyValueStore = (KeyValueStore) registry.lookup("KeyValueStore");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Choose an operation: PUT, GET, DELETE, EXIT");
                String operation = scanner.nextLine().toUpperCase();
                switch (operation) {
                    case "PUT":
                        System.out.println("Enter key:");
                        String putKey = scanner.nextLine();
                        System.out.println("Enter value:");
                        String putValue = scanner.nextLine();
                        System.out.println(keyValueStore.put(putKey, putValue));
                        break;
                    case "GET":
                        System.out.println("Enter key:");
                        String getKey = scanner.nextLine();
                        System.out.println(keyValueStore.get(getKey));
                        break;
                    case "DELETE":
                        System.out.println("Enter key:");
                        String deleteKey = scanner.nextLine();
                        System.out.println(keyValueStore.delete(deleteKey));
                        break;
                    case "EXIT":
                        System.out.println("Exiting client.");
                        System.exit(0);
                    default:
                        System.out.println("Invalid operation. Please try again.");
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
