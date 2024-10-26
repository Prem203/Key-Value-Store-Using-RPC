# Multi-Threaded Key-Value Store using RPC

## Overview
This project is a multi-threaded key-value store implemented using Remote Procedure Calls (RPC). The server can handle concurrent requests from multiple clients, allowing for efficient PUT, GET, and DELETE operations on key-value pairs. The project aims to demonstrate the implementation of synchronization techniques to manage concurrent client access and ensure data consistency.

## System Requirements
- **Java Development Kit (JDK)**: Version 11 or higher
- **Docker**: Version 20.10 or higher (for containerizing server and client components)
- **Git**: To clone the repository
- **Internet Connection**: To download necessary images and packages

## Getting Started
Follow these steps to get the project up and running on your system:

### 1. Clone the Repository
```bash
$ git clone https://github.com/yourusername/multi-threaded-kv-store-rpc.git
$ cd multi-threaded-kv-store-rpc
```

### 2. Build Docker Images
Build Docker images for both the server and client components.
```bash
$ docker build -t kv_server -f Dockerfile.server .
$ docker build -t kv_client -f Dockerfile.client .
```

### 3. Create a Docker Network
Create a Docker network to allow communication between the server and clients.
```bash
$ docker network create kv-network
```

### 4. Run the Server
Start the server container, attaching it to the Docker network.
```bash
$ docker run --name kv_server --network kv-network -d kv_server
```

### 5. Run Multiple Clients
Run multiple client containers to test the concurrency of the key-value store.
```bash
$ docker run --name kv_client1 --network kv-network -d kv_client
$ docker run --name kv_client2 --network kv-network -d kv_client
$ docker run --name kv_client3 --network kv-network -d kv_client
$ docker run --name kv_client4 --network kv-network -d kv_client
$ docker run --name kv_client5 --network kv-network -d kv_client
```

### 6. Check Logs
To verify the server's performance and see the operations being processed, check the server logs.
```bash
$ docker logs kv_server
```

## Usage
- The server handles PUT, GET, and DELETE requests from multiple clients concurrently.
- Each client performs 5 PUT, 5 GET, and 5 DELETE operations.
- The server logs each request, showing which client made the request and whether it was successful.

## Key Features
- **Concurrency Handling**: The server uses synchronization techniques to handle multiple concurrent client requests without data races.
- **Docker Containerization**: The use of Docker ensures that the server and clients can run in isolated environments, making setup easy and consistent.
- **Scalable Design**: The server can handle multiple clients, demonstrating its scalability in a distributed environment.

## Troubleshooting
- **Port Already in Use**: If you encounter an error like `Port already in use`, ensure that no other process is using the default RMI port (1099). You can stop any existing server container or change the port number.
- **Connection Refused**: Ensure that the Docker network is correctly set up and that both server and client containers are attached to the same network.

## Cleanup
To stop and remove all containers:
```bash
$ docker stop kv_server kv_client1 kv_client2 kv_client3 kv_client4 kv_client5
$ docker rm kv_server kv_client1 kv_client2 kv_client3 kv_client4 kv_client5
```
To remove the Docker network:
```bash
$ docker network rm kv-network
```

## License
This project is licensed under the MIT License.

## Acknowledgements
- Inspired by distributed systems concepts and RPC mechanisms.

