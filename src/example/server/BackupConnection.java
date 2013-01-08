package example.server;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class BackupConnection extends Thread {

    private DatagramSocket serverSocket;

    public BackupConnection(int port) {
        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("SocketException caught.");
        }
    }

    @Override
    public void run() {
        super.run();
        byte[] buffer = new byte[1];
        buffer[0] = 1;
        System.out.println("Backup connection ON.");
        while (true) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            try {
                serverSocket.receive(request);

                DatagramPacket reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                serverSocket.send(reply);

                System.out.println("Alive message sent.");
            } catch (IOException e) {
                System.out.println("IOException caught.");
            }

        }
    }
}