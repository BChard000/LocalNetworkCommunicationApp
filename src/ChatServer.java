import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private int port;
    // A set to store usernames to ensure each is unique
    private Set<String> userNames = new HashSet<>();
    // A set of user threads, one for each connected client
    private Set<ClientThread> userThreads = new HashSet<>();

    // Constructor that initializes the server with a given port
    public ChatServer(int port) {
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat Server is listening on port " + port);

            // Server enters an infinite loop, constantly listening for new clients
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                // Creating a new thread for the newly connected client
                ClientThread newUser = new ClientThread(socket, this);
                // Adding the new client thread to the set of user threads
                userThreads.add(newUser);
                newUser.start();
            }
        } catch (IOException ex) {
            // Print any server errors
            System.out.println("Server error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Method to broadcast a message to all clients except the sender
    void broadcast(String message, ClientThread excludeUser) {
        for (ClientThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    // Main method to start the server
    public static void main(String[] args) {
        // Check for correct usage and get the port number from command line arguments
        if (args.length < 1) {
            System.out.println("Syntax: java ChatServer <port-number>");
            System.exit(0);
        }

        // Parse the port number from the command line argument
        int port = Integer.parseInt(args[0]);

        // Create a new ChatServer instance and start it
        ChatServer server = new ChatServer(port);
        server.execute();
    }
}