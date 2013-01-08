package example.server;


import java.net.*;
import java.io.*;

public class TCPClientThread extends Thread {

    private Socket socket = null;
    private TCPClient client = null;
    private DataInputStream streamIn = null;

    public TCPClientThread(TCPClient _client, Socket _socket) {
        this.client = _client;
        this.socket = _socket;
        open();
        start();
    }

    public void open() {
        try {
            streamIn = new DataInputStream(socket.getInputStream());

        } catch (IOException ioe) {
            System.out.println("Error getting input stream: " + ioe);
            client.stop();
        }
    }

    public void close() {
        try {
            if (streamIn != null) {
                streamIn.close();
            }
        } catch (IOException ioe) {
            System.out.println("Error closing input stream: " + ioe);
        }
    }

    public void run() {
        while (true) {
            try {

                client.handle(streamIn.readUTF());


            } catch (IOException ioe) {
                try {
                    this.socket.close();
                } catch (IOException e1) {
                }
                client.reConnect();
            }

        }
    }

    public DataInputStream getStreamIn() {
        return this.streamIn;
    }

    public void setStreamIn(DataInputStream streamIn) {
        this.streamIn = streamIn;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}