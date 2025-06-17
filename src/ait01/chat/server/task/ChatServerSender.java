package ait01.chat.server.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import java.util.HashMap;
import java.util.Map;

public class ChatServerSender implements Runnable {
    private final BlockingQueue<String> messageBox;
    private final Map<Socket, PrintWriter> clients;

    public ChatServerSender(BlockingQueue<String> messageBox) {
        this.messageBox = messageBox;
        clients = new HashMap<>();
    }

    public synchronized boolean addClient(Socket socket) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        clients.put(socket, writer);
        System.out.println("Client added. Total clients: " + clients.size());
        return true;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = messageBox.take();
                synchronized (this) {
                    Iterator<Map.Entry<Socket, PrintWriter>> iterator = clients.entrySet().iterator();

                    while (iterator.hasNext()) {
                        Map.Entry<Socket, PrintWriter> entry = iterator.next();
                        Socket socket = entry.getKey();
                        PrintWriter writer = entry.getValue();

                        // Проверяем состояние сокета и отправляем сообщение
                        if (socket.isClosed() || !isClientActive(writer, message)) {
                            System.out.println("Removing client: " + socket.getInetAddress() + ":" + socket.getPort());
                            closeClient(socket, writer);
                            iterator.remove();
                        }
                    }

                    System.out.println("Active clients: " + clients.size());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Проверяем активность клиента, отправляя сообщение
    private boolean isClientActive(PrintWriter writer, String message) {
        writer.println(message);
        return !writer.checkError(); // Если ошибка - клиент неактивен
    }

    // Закрываем соединение с клиентом
    private void closeClient(Socket socket, PrintWriter writer) {
        try {
            writer.close();
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // Игнорируем ошибки при закрытии
        }
    }
}