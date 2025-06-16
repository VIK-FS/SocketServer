package ait.chat.server;

import ait.chat.server.task.ChatServerReciever;
import ait.chat.server.task.ChatServerSender;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ChatServerAppl {
    public static void main(String[] args) throws InterruptedException {
        int port = 9000;
        BlockingQueue<String> messageBox = new ArrayBlockingQueue<>(10);
        ChatServerSender sender = new ChatServerSender(messageBox);
        Thread senderThread = new Thread(sender);
        senderThread.setDaemon(true);
        senderThread.start();
        ExecutorService executorService = Executors.newCachedThreadPool();
//        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            while (true) {
                System.out.println("Server waiting...");
                Socket socket = serverSocket.accept();
                System.out.println("Connection established");
                System.out.println("Client host: " + socket.getInetAddress() + ":" + socket.getPort());
                sender.addClient(socket);
                ChatServerReciever reciever = new ChatServerReciever(socket, messageBox);
                executorService.execute(reciever);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(30, TimeUnit.SECONDS);
            System.out.println("Server finished");
        }
    }
}
