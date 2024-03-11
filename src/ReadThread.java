import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {
    // BufferedReader to read messages from the server
    private BufferedReader reader;
    // Reference to the ChatClient, used to call a method to handle received messages
    private final ChatClient client;

    // Constructor initializes the ReadThread with a Socket and the ChatClient
    public ReadThread(Socket socket, ChatClient client) {
        this.client = client; // Set the client instance
        try {
            // Initialize the BufferedReader to read from the socket's input stream
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            // Handle exceptions related to obtaining the input stream
            System.out.println("Input stream error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // The run method contains the logic executed on the thread
    public void run() {
        while (true) { // Continuously listen for messages
            try {
                // Read a line of text from the server
                String response = reader.readLine();
                if (response != null) {
                    // If a message is received, notify the client instance
                    client.messageReceived(response);
                } else {
                    // Handle null response when server closes the connection
                    System.out.println("Connection closed.");
                    break;
                }
            } catch (IOException e) {
                // Handle errors during reading from the input stream
                System.out.println("Error reading from server: " + e.getMessage());
                break;
            }
        }
    }
}