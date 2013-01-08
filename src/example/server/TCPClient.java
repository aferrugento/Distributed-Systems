package example.server;


import java.net.*;
import java.io.*;

public class TCPClient implements Runnable {

    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private int serverPort;
    private String serverName;
    private Boolean login;
    private TCPClientThread client = null;
    private int user;
    private Boolean flagImage;

    public TCPClient(String serverName) {
        this.serverName = serverName;
        this.serverPort = 6000;
        this.login = false;
        this.flagImage = false;
        if (!this.Connect(true)) {
            System.exit(1);
        }

    }

    public synchronized void reConnect() {
        try {
            if (this.renewSocket(false)) {
                return;
            } else {
                renewSocket(true);
            }
        } catch (InterruptedException e) {
            return;
        }

    }

    public boolean renewSocket(boolean ServerDown) throws InterruptedException {
        if (!ServerDown) {
            for (int i = 0; i < 6; i++) {
                Thread.sleep(500);
                if (this.Connect(ServerDown)) {
                    return true;
                }
            }
        } else {
            System.err.println("Server is down.");
            while (true) {
                System.err.println("Working on it...");
                Thread.sleep(5000);
                this.serverPort = 6000;
                if (this.Connect(ServerDown)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean Connect(boolean svdown) {
        try {
            this.socket = new Socket(this.serverName, this.serverPort);

        } catch (UnknownHostException e) {
            if (svdown) {
                try {
                    this.socket = new Socket(this.serverName, ++this.serverPort);
                } catch (UnknownHostException e2) {
                    return false;
                } catch (IOException e2) {
                    return false;
                }
            } else {
                return false;
            }


        } catch (IOException e) {

            if (svdown) {
                try {
                    this.socket = new Socket(this.serverName, ++this.serverPort);
                } catch (UnknownHostException e2) {
                    return false;
                } catch (IOException e2) {
                    return false;
                }
            } else {
                return false;
            }

        }
        try {
            System.out.println("Connected: " + socket);
            start();
        } catch (IOException ioe) {
            System.out.println("ups, something went wrong.");
        }
        String temp;
        this.setStreams();

        if (this.login) {
            try {
                temp = this.user + "";
                this.streamOut.writeUTF(temp);
            } catch (IOException ioe) {
                System.out.println("Error writing on socket.");
            }
        } else {
            try {
                this.streamOut.writeUTF("Not logged in!!");
            } catch (IOException ioe) {
                System.out.println("Error writing on socket.");
            }
        }
        return true;
    }

    public void setStreams() {
        try {

            this.client.setStreamIn(new DataInputStream(this.socket.getInputStream()));
            this.streamOut = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException ioe) {
            System.out.println("ups, something went wrong.");
            setStreams();
        }

    }

    public void run() {
        while (thread != null) {
            try {

                String temp = console.readLine();
                if (this.flagImage) {
                    ASCIIConverter conv = new ASCIIConverter();
                    String o = conv.convertAndResize(temp);
                    streamOut.writeUTF(o);
                    streamOut.flush();
                    this.flagImage = false;
                } else {
                    streamOut.writeUTF(temp);
                    streamOut.flush();
                }
            } catch (IOException ioe) {
                System.out.println("Error writing on socket.");
            }




        }
    }

    public void handle(String msg) {

        String temp = null;
        if (msg.equals("Logging out!!")) {
            System.out.println("Good bye. Press RETURN to exit ...");
            stop();
        } else if (msg.equals("Logging in!!")) {
            this.login = true;
            try {
                this.user = Integer.parseInt(client.getStreamIn().readUTF());
            } catch (IOException ioe) {
                System.out.println("Error reading from socket.");
            }
        } else if (msg.equals("Enter the image path: ")) {
            System.out.println(msg);
            this.flagImage = true;
        } else {
            System.out.println(msg);
        }
    }

    public void start() throws IOException {
        console = new DataInputStream(System.in);
        if (thread == null) {
            client = new TCPClientThread(this, socket);
            thread = new Thread(this);
            client.setSocket(this.socket);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }

        try {
            if (console != null) {
                console.close();
            }
            if (streamOut != null) {
                streamOut.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            System.out.println("Error closing ...");
        }

        client.close();
        client.stop();
    }

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: java TCPClient hostname.");
        } else {
            new TCPClient(args[0]);
        }
    }
}