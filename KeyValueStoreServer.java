import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyValueStoreServer extends UnicastRemoteObject implements KeyValueStoreInterface {
    private final Map<String, String> store = new HashMap<>();
    private final int serverId;
    private final List<String> replicas;
    private final AtomicInteger operationsToExecute = new AtomicInteger(0); // Track active operations
    private final Timer shutdownCounter = new Timer(); // Timer for graceful shutdown
    private int proposalCounter = 0;
    private int highestPromised = 0;
    private int highestProposalVal = 0;
    private String acceptedVal = null;
    private final Random random = new Random();

    public KeyValueStoreServer(int serverId, List<String> replicas) throws RemoteException {
        this.serverId = serverId;
        this.replicas = replicas;
        checkForShutdown();
    }

    private void checkForShutdown() {
    shutdownCounter.schedule(new TimerTask() {
        @Override
        public void run() {
            if (operationsToExecute.get() == 0) {
                System.out.println("Server " + serverId + " shutting down gracefully due to inactivity.");
                shutdownServer();
            }
        }
    }, 30000, 30000); // Check every 30 seconds
}

private void shutdownServer() {
    try {
        UnicastRemoteObject.unexportObject(this, true);
        shutdownCounter.cancel();
        System.out.println("Server " + serverId + " stopped successfully.");
    } catch (Exception e) {
        System.err.println("Server " + serverId + " - Error during shutdown: " + e.getMessage());
    }
}


    @Override
    public String get(String key) throws RemoteException {
        operationsToExecute.incrementAndGet();
        System.out.println("Server " + serverId + " - GET operation: Key = " + key);
        String result = store.getOrDefault(key, null);
        operationsToExecute.decrementAndGet();
        return result;
    }

    @Override
    public boolean put(String key, String value) throws RemoteException {
        operationsToExecute.incrementAndGet();
        System.out.println("Server " + serverId + " - PUT operation: Key = " + key + ", Value = " + value);
        boolean result = performRetries("PUT:" + key + ":" + value);
        operationsToExecute.decrementAndGet();
        return result;
    }

    @Override
    public boolean delete(String key) throws RemoteException {
        operationsToExecute.incrementAndGet();
        System.out.println("Server " + serverId + " - DELETE operation: Key = " + key);
        boolean result = performRetries("DELETE:" + key);
        operationsToExecute.decrementAndGet();
        return result;
    }

    private boolean performRetries(String operation) {
        int retries = 5; // Retry up to 5 times
        while (retries > 0) {
            int proposalNumber = randomProposalGeneration(); // Incrementing proposal number
            System.out.println("Server " + serverId + " - Starting Paxos for operation: " + operation
                    + " with proposal: " + proposalNumber);

            if (executePaxos(proposalNumber, operation)) {
                System.out.println("Server " + serverId + " - Operation performed successfully: " + operation);
                return true;
            }

            System.out
                    .println("Server " + serverId + " - Operation failed: " + operation + ". Retrying...");
            retries--;
        }

        System.out.println(
                "Server " + serverId + " - Operation terminated, "+retries+" retries left: " + operation);
        return false;
    }

    private int randomProposalGeneration() {
        proposalCounter++;
        return proposalCounter * 1000 + serverId; // Ensures unique and incrementing proposal numbers
    }

    private boolean executePaxos(int proposalNumber, String operation) {
        // Phase 1: Prepare
        int prepareAckCount = 0;
        for (String replica : replicas) {
            try {
                String[] parts = replica.split(":");
                Registry registry = LocateRegistry.getRegistry(parts[0], Integer.parseInt(parts[1]));
                KeyValueStoreInterface replicaStub = (KeyValueStoreInterface) registry.lookup("KeyValueStore");

                if (replicaStub.prepare(proposalNumber, operation)) {
                    prepareAckCount++;
                }
            } catch (Exception e) {
                System.out.println(
                        "Server " + serverId + " - Prepare failed for replica " + replica + ": " + e.getMessage());
            }
        }

        System.out.println("Server " + serverId + " - Prepare phase completed. Acknowledgements: " + prepareAckCount);

        if (prepareAckCount <= replicas.size() / 2) {
            System.out.println("Server " + serverId + " - Prepare phase failed.");
            return false;
        }

        // Phase 2: Accept
        int acceptAckCount = 0;
        for (String replica : replicas) {
            try {
                String[] parts = replica.split(":");
                Registry registry = LocateRegistry.getRegistry(parts[0], Integer.parseInt(parts[1]));
                KeyValueStoreInterface replicaStub = (KeyValueStoreInterface) registry.lookup("KeyValueStore");

                if (replicaStub.accept(proposalNumber, operation)) {
                    acceptAckCount++;
                }
            } catch (Exception e) {
                System.out.println(
                        "Server " + serverId + " - Accept failed for replica " + replica + ": " + e.getMessage());
            }
        }

        System.out.println("Server " + serverId + " - Accept phase completed. Acknowledgements: " + acceptAckCount);

        if (acceptAckCount <= replicas.size() / 2) {
            System.out.println("Server " + serverId + " - Accept phase failed.");
            return false;
        }

        // Phase 3: Learn
        for (String replica : replicas) {
            try {
                String[] parts = replica.split(":");
                Registry registry = LocateRegistry.getRegistry(parts[0], Integer.parseInt(parts[1]));
                KeyValueStoreInterface replicaStub = (KeyValueStoreInterface) registry.lookup("KeyValueStore");

                replicaStub.learn(operation);
            } catch (Exception e) {
                System.out.println(
                        "Server " + serverId + " - Learn failed for replica " + replica + ": " + e.getMessage());
            }
        }

        System.out.println("Server " + serverId + " - Learn phase completed.");
        applyOperation(operation);
        return true;
    }

    @Override
    public boolean prepare(int proposalNumber, String operation) throws RemoteException {
        synchronized (this) {
            // Simulate random failure
            if (random.nextDouble() < 0.3) { // 30% chance to fail
                System.out.println("Server " + serverId + " - Simulating failure during Prepare phase for proposal: "
                        + proposalNumber);
                return false;
            }

            if (proposalNumber > highestPromised) {
                highestPromised = proposalNumber;
                System.out
                        .println("Server " + serverId + " - Promise made for proposal: " + proposalNumber);
                return true;
            }

            System.out.println("Server " + serverId + " - Rejected proposal: " + proposalNumber
                    + " (Highest Promised: " + highestPromised + ")");
            return false;
        }
    }

    @Override
    public boolean accept(int proposalNumber, String operation) throws RemoteException {
        synchronized (this) {
            // Simulate random failure
            if (random.nextDouble() < 0.3) { // 30% chance to fail
                System.out.println("Server " + serverId + " - Simulating failure during Accept phase for proposal: "
                        + proposalNumber);
                return false;
            }

            if (proposalNumber >= highestPromised) {
                highestPromised = proposalNumber;
                highestProposalVal = proposalNumber;
                acceptedVal = operation;
                System.out.println("Server " + serverId + " - Accepted proposal: " + proposalNumber);
                return true;
            }

            System.out.println("Server " + serverId + " - Rejected proposal: " + proposalNumber
                    + " (Highest Promised: " + highestPromised + ")");
            return false;
        }
    }

    @Override
    public void learn(String operation) throws RemoteException {
        applyOperation(operation);
    }

    private void applyOperation(String operation) {
        String[] parts = operation.split(":");
        String type = parts[0];
        String key = parts[1];
        String value = parts.length > 2 ? parts[2] : null;

        synchronized (this) {
            switch (type) {
                case "PUT":
                    store.put(key, value);
                    System.out.println("Server " + serverId + " - Key added: " + key + " = " + value);
                    break;
                case "DELETE":
                    store.remove(key);
                    System.out.println("Server " + serverId + " - Key deleted: " + key);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        try {
            String host = InetAddress.getLocalHost().getHostName();
            int port = Integer.parseInt(System.getenv("PORT"));
            int serverId = Integer.parseInt(System.getenv("SERVER_ID"));
            String replicas = System.getenv("REPLICAS");

            List<String> replica = parsereplicas(replicas);

            KeyValueStoreServer server = new KeyValueStoreServer(serverId, replica);
            LocateRegistry.createRegistry(port);
            Naming.rebind("//" + host + ":" + port + "/KeyValueStore", server);

            System.out.println("KeyValueStoreServer started on " + host + ":" + port);
        } catch (Exception e) {
            System.out.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<String> parsereplicas(String replicas) {
        return replicas == null || replicas.isEmpty() ? new ArrayList<>() : Arrays.asList(replicas.split(","));
    }
}