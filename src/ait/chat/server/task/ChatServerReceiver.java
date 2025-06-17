package ait.chat.server.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ChatServerReceiver implements Runnable{
    private final Socket socket;
    private final BlockingQueue<String> messageBox;

    public ChatServerReceiver(Socket socket, BlockingQueue<String> messageBox) {
        this.socket = socket;
        this.messageBox = messageBox;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket) {
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String message = socketReader.readLine();
                if (message == null) {
//                    System.out.println("Connection: " + socket.getInetAddress() + ":" + socket.getPort() + ", closed");
//                    break;
                    throw new IOException();
                }
                messageBox.put(message);
            }
        } catch (IOException e) {
            System.out.println("Connection: " + socket.getInetAddress() + ":" + socket.getPort() + ", closed");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}