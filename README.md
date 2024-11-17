# Multi-Threaded Key-Value Store using RPC

his project implements a distributed key-value store using Java RMI (Remote Method Invocation). The system supports basic CRUD operations (`PUT`, `GET`, and `DELETE`) with fault-tolerant replication and ensures data consistency using the two-phase commit protocol.

## **Features**
1. **Distributed Architecture**:
   - Multiple server replicas hosted in separate containers for fault tolerance.
   - Client dynamically connects to an available replica.

2. **Two-Phase Commit Protocol**:
   - Ensures atomicity and consistency across all replicas during write/delete operations.

3. **Replication**:
   - Data is replicated across 5 servers (default configuration).
   - Each server operates independently, synchronized using two-phase commit.

4. **Concurrency**:
   - Supports concurrent operations using thread-safe data structures and locks.

5. **Containerized Deployment**:
   - Uses Docker and Docker Compose for easy setup and scaling.

---

## **Setup**

### **Prerequisites**
- **Java 8 or higher**: For running the server and client code.
- **Docker**: For containerized deployment.

---

### **Steps to Run**

#### **1. Clone the Repository**
```bash
git clone https://github.com/Prem203/Key-Value-Store-Using-RPC.git
git checkout proj-3
cd key-value-store-using-rpc
```

#### **2. Build and Run Containers**
- Use Docker Compose to start the containers:
```bash
docker-compose up --build
```

- This will spin up:
  - 5 server containers (`kv_server_0`, `kv_server_1`, ..., `kv_server_4`).
  - 1 client container (`kv_client`).

#### **3. Interact with the Key-Value Store**
- The client performs the following operations during execution:
  - Pre-populates the key-value store.
  - Executes 5 `PUT` operations, 5 `GET` operations, and 5 `DELETE` operations.
- Logs are available in the console output of the respective containers.

---

## **Directory Structure**

```
key-value-store-using-rpc/
├── docker-compose.yml                 # Docker Compose configuration
├── Dockerfile.client                  # Dockerfile for client container
├── Dockerfile.server                  # Dockerfile for server containers
├── KeyValueStore.java                 # Interface for CRUD operations
├── KeyValueStoreClient.java           # Client implementation
├── KeyValueStoreServer.java           # Server implementation
├── LoggerUtility.java                 # Utility for structured logging
├── LICENSE                            # License information
├── README.md                          # Project documentation
```

---

## **Detailed Workflow**

### **1. Server Initialization**
- The server initializes 5 replicas.
- Each replica is assigned a unique ID (`Replica 0`, `Replica 1`, etc.).
- A primary replica (`Replica 0`) is bound as `KeyValueStore` for default access.

### **2. Client Operations**
- The client randomly connects to one of the 5 replicas.
- Executes the following sequence of operations:
  1. **Pre-Populate**:
     - Adds initial key-value pairs to the store.
  2. **PUT**:
     - Adds or updates key-value pairs.
  3. **GET**:
     - Retrieves values for given keys.
  4. **DELETE**:
     - Deletes specific keys from the store.

### **3. Two-Phase Commit**
- For `PUT` and `DELETE` operations:
  - **Phase 1 (Prepare)**:
    - All replicas are notified of the operation.
  - **Phase 2 (Commit)**:
    - Operation is committed on all replicas if all prepare successfully.
    - Logs include replica IDs to distinguish between servers.

---

## **Logging and Debugging**
- Logs are generated using `LoggerUtility` for better traceability.
- Logs include:
  - Operation type (`PUT`, `GET`, or `DELETE`).
  - Key and value details.
  - Status (e.g., "Preparing", "Committed").
  - Replica-specific messages.

### Sample Logs
```plaintext
kv_server_2 | [2024-11-17 13:20:04] Received DELETE request for key: city
kv_server_2 | [2024-11-17 13:20:04] Initiating Two-Phase Commit for operation: DELETE on key: city
kv_server_2 | [2024-11-17 13:20:04] Preparing DELETE on replica for key: city
kv_server_2 | [2024-11-17 13:20:04] DELETE committed for key: city
kv_server_2 | [2024-11-17 13:20:04] Committing DELETE on replica for key: city
```

---

## **License**
This project is licensed under the MIT License. See the `LICENSE` file for details.