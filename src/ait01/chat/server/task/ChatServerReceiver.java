package ait01.chat.server.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ChatServerReceiver implements Runnable {
    private final Socket socket;
    private final BlockingQueue<String> messageBox;

    public ChatServerReceiver(Socket socket, BlockingQueue<String> messageBox) {
        this.socket = socket;
        this.messageBox = messageBox;
    }

    @Override
    public void run() {
        String clientInfo = socket.getInetAddress() + ":" + socket.getPort();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = reader.readLine()) != null) {
                if (!message.trim().isEmpty()) {
                    messageBox.put("[" + clientInfo + "]: " + message);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Client disconnected: " + clientInfo);
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // Игнорируем ошибки при закрытии
            }
        }
    }
}