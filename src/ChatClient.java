import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    // GUI components
    private JTextArea textArea;
    private JTextField inputField;
    private PrintWriter writer; // Writer to send messages to the server
    private Socket socket; // Socket to connect to the server

    // Constructor initializes the GUI and starts the client's execution
    public ChatClient(String hostname, int port) {
        initializeGUI(); // Initialize the graphical user interface
        execute(hostname, port); // Connect to the server and start the reader thread
    }

    // Connects to the server and sets up the reader and writer
    private void execute(String hostname, int port) {
        try {
            this.socket = new Socket(hostname, port); // Connect to the server
            this.writer = new PrintWriter(socket.getOutputStream(), true); // Set up the writer

            // Starting the ReadThread to listen for messages from the server
            new ReadThread(socket, this).start();
        } catch (IOException e) {
            displayMessage("Connection failed"); // Display a connection error message
            e.printStackTrace();
        }
    }

    // Sends a message to the server
    public void sendMessage(String msg) {
        writer.println(msg);
    }

    // Called when a message is received from the server
    public void messageReceived(String message) {
        displayMessage(message);
    }

    // Displays a message in the text area
    public void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }

    // Initializes the graphical user interface
    private void initializeGUI() {

        JFrame frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);

        // Custom colors for the GUI
        Color backgroundColor = new Color(45, 45, 45);
        Color textColor = new Color(200, 200, 200);
        Color inputBackgroundColor = new Color(60, 63, 65);
        Color inputTextColor = Color.WHITE;

        // Setting up the text area
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(backgroundColor);
        textArea.setForeground(textColor);
        textArea.setCaretColor(textColor);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        scrollPane.setBackground(backgroundColor);

        // Setting up the input field
        inputField = new JTextField();
        inputField.setBackground(inputBackgroundColor);
        inputField.setForeground(inputTextColor);
        inputField.setCaretColor(inputTextColor);

        // Setting up the send button
        JButton sendButton = new JButton("Send");
        sendButton.setBackground(inputBackgroundColor);
        sendButton.setForeground(inputTextColor);
        sendButton.addActionListener((ActionEvent e) -> {
            sendMessage(inputField.getText()); // Send message on button click
            inputField.setText(""); // Clear the input field after sending
        });

        // Input panel contains the input field and the send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(backgroundColor);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to the frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.getContentPane().setBackground(backgroundColor);
        frame.setVisible(true);
    }
}