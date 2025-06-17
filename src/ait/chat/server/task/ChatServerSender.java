package ait.chat.server.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class ChatServerSender implements Runnable {
    private final BlockingQueue<String> messageBox;
    private final Set<PrintWriter> clients;

    public ChatServerSender(BlockingQueue<String> messageBox) {
        this.messageBox = messageBox;
        clients = new HashSet<>();
    }

    public synchronized boolean addClient(Socket socket) throws IOException {
        return clients.add(new PrintWriter(socket.getOutputStream(), true));
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = messageBox.take();
                synchronized (this) {
                    Iterator<PrintWriter> iterator = clients.iterator();
                    while (iterator.hasNext()) {
                        PrintWriter clientWriter = iterator.next();
                        if (clientWriter.checkError()) {
                            iterator.remove();
                        } else {
                            clientWriter.println(message);
                        }
                    }
                    System.out.println("size = " + clients.size());
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}