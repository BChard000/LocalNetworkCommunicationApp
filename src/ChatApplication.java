import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatApplication {
    // GUI components for displaying and entering text
    private JTextArea textArea;
    private JTextField inputField;
    // Writer to send messages to the server
    private PrintWriter writer;
    // Socket for network connection to the server
    private Socket socket;
    // Server details
    private String hostname;
    private int port;

    // Constructor initializes the chat application with server details
    public ChatApplication(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        initializeGUI();
    }

    // Setting up the graphical user interface
    private void initializeGUI() {
        JFrame frame = new JFrame("Chat Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);

        // Text area for displaying chat messages
        textArea = new JTextArea(20, 50);
        textArea.setEditable(false);

        // Input field for typing messages
        inputField = new JTextField(50);
        // Send button with action listener to send messages
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener((ActionEvent e) -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                sendMessage(message); // Send message if not empty
                inputField.setText(""); // Clear input field
            }
        });

        // Scroll pane for text area to enable scrolling
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel containing the input field and send button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // Add components to the frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Connect to the server and set up the input and output streams
    public void execute() {
        try {
            socket = new Socket(hostname, port); // Connect to the server
            writer = new PrintWriter(socket.getOutputStream(), true); // Set up the writer

            // Start a new thread for reading messages from the server
            new Thread(new ReadThread(socket, this)).start();
        } catch (IOException ex) {
            // Show error message if connection fails
            JOptionPane.showMessageDialog(null, "Error connecting to the server: " + ex.getMessage(),
                    "Connection error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Send message to the server
    private void sendMessage(String message) {
        writer.println(message);
    }

    // Display messages from the server
    public void displayMessage(final String message) {
        // Ensure updates to the GUI are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        ChatApplication client = new ChatApplication("localhost", 9998);
        // Start the application within the Event Dispatch Thread
        SwingUtilities.invokeLater(client::execute);
    }

    // Inner class to handle reading messages from the server
    private class ReadThread implements Runnable {
        private BufferedReader reader;
        private Socket socket;
        private ChatApplication client;

        // Constructor initializes the reader thread with the client socket
        public ReadThread(Socket socket, ChatApplication client) {
            this.socket = socket;
            this.client = client;

            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // Continuously read messages from the server and display them
        @Override
        public void run() {
            while (true) {
                try {
                    String response = reader.readLine();
                    if (response != null) {
                        client.displayMessage(response); // Display each received message
                    } else {
                        break; // Break the loop if the server closes the connection
                    }
                } catch (IOException ex) {
                    // Print errors and exit the loop if reading fails
                    System.out.println("Error reading from server: " + ex.getMessage());
                    ex.printStackTrace();
                    break;
                }
            }
        }
    }
}