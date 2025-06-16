package ait.socket.server.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClietHandler implements Runnable {
    private Socket socket;

    public ClietHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket){
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                String message = socketReader.readLine();
                if (message == null) {
                    System.out.println("Connection: " + socket.getInetAddress() + ":" + socket.getPort() + ",closed ");
                    break;
                }
                System.out.println("Server received: " + message);
                socketWriter.println(message + " " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")));
            }
        } catch (IOException e) {
            System.out.println("Connection: " + socket.getInetAddress() + ":" + socket.getPort() + ",closed ");
        }
    }
}
