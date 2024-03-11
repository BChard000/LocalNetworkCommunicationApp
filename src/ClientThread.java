import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    // Socket for the connection to the client
    private Socket socket;
    // Reference to the chat server to call methods on it
    private ChatServer server;
    // Writer to send messages back to the client
    private PrintWriter writer;

    // Constructor to initialize the client thread with its socket and server reference
    public ClientThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            // Set up reading from the client
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // Set up writing to the client
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            // First message from the client is the username
            String userName = reader.readLine();
            // Broadcast to all clients that a new user has joined
            server.broadcast(userName + " has joined", this);

            String serverMessage;
            String clientMessage;

            // Continuously read messages sent by the client and broadcast them
            do {
                clientMessage = reader.readLine(); // Read the message from the client
                serverMessage = userName + ": " + clientMessage; // Format it for broadcast
                server.broadcast(serverMessage, this); // Broadcast the message
            } while (!clientMessage.equals("bye")); // Continue until the client sends "bye"

            // After "bye", broadcast that the user has left and close the socket
            server.broadcast(userName + " has left.", this);
            socket.close();
        } catch (IOException ex) {
            // Print any errors encountered during communication with the client
            System.out.println("Error in ClientThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Helper method to send a message to this client only
    void sendMessage(String message) {
        writer.println(message);
    }
}