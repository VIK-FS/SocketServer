package ait.chat.server.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ChatServerReciever implements Runnable {
    private final Socket socket;
    private final BlockingQueue<String> messageBox;

    public ChatServerReciever(Socket socket, BlockingQueue<String> messageBox) {
        this.socket = socket;
        this.messageBox = messageBox;
    }

    @Override
    public void run() {
        // TODO homework
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))) {

            String message;
            // Read messages from client while connection is active
            while ((message = reader.readLine()) != null) {
                System.out.println("Received message: " + message);

                // Add message to queue for broadcasting to all clients
                try {
                    messageBox.put(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.printf("Reciever interrupted");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.printf("Client disconnected" + socket.getInetAddress());
        } finally {
            // Close socket on completion
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.printf("Error closing socket" + e.getMessage());
            }
        }
    }
}

