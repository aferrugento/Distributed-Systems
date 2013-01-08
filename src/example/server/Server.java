package example.server;


import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RMISecurityManager;

public class Server {

    public static FileThread file;
    private int serverPort;
    private String hostname;
    private BackupConnection backupLink;
    private TCPServer serverTCP;
    private ServerRMI serverRMI;

    private void waitMainServer() {
        int port = (this.serverPort == 6000 ? 6001 : 6000);
        int counter = 0;

        while (counter < 3) {
            if (this.CheckMain(this.hostname, port)) {
                counter = 0;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("ups, something went wrong.");
                    this.waitMainServer();
                }
            } else {
                counter++;
            }
        }
    }

    private void startServer() {
        this.backupLink = new BackupConnection(serverPort);
        this.backupLink.start();


        serverTCP = new TCPServer(this.serverPort);
        try {
            this.serverRMI = new ServerRMI();
            
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("SayMore", this.serverRMI);
            System.out.println("Starting RMIServer.");
        } catch (RemoteException re) {
            System.out.println("Remote exception: " + re);
            startServer();
        }
        serverTCP.clients = new Vector<TCPServerThread>();
        serverTCP.users = new Vector<User>();
        serverTCP.posts = new ConcurrentHashMap<Integer, Post>();
        serverTCP.messages = new Vector<Message>();
        serverTCP.countID = -1;
        (serverTCP.delayedPosts = new Delay(this.serverTCP)).start();

        serverRMI.clients = new Vector<ClientRMIConnection>();
        serverRMI.users = new Vector<User>();
        serverRMI.setPosts(new ConcurrentHashMap<Integer, Post>());
        serverRMI.messages = new Vector<Message>();
        serverRMI.countID = -1;
        (serverRMI.delayedPosts = new DelayR(this.serverRMI)).start();

        file = new FileThread(this);
        file.start();

        System.out.println("Loading data.");


    }

    private boolean CheckMain(String host, int port) {
        DatagramSocket aSocket = null;
        InetAddress aHost;
        try {

            aHost = InetAddress.getByName(host);
            byte[] buffer = new byte[1];
            buffer[0] = 2;
            aSocket = new DatagramSocket();
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, aHost, port);

            aSocket.send(request);
            System.out.println("Request survivor status: " + host + " " + port + ".");
            aSocket.setSoTimeout(1000);
            while (true) {
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);
                if (reply.getPort() == port && reply.getAddress().equals(aHost)) {
                    aSocket.close();
                    return true;
                }
            }
        } catch (SocketException e) {
            aSocket.close();
        } catch (IOException e) {
            aSocket.close();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public Vector<User> loadUsersTCPInfo(ObjectFile object) throws ClassNotFoundException {
        //Users
        File f = new File("UsersTCP.dat");

        if (!f.exists()) {
            return this.serverTCP.users;
        }
        try {
            object.openRead("UsersTCP.dat");

            serverTCP.users = (Vector<User>) object.readObject();

            object.closeRead();
        } catch (IOException ioe) {
            System.out.println("Couldn't load users info: " + ioe);
            this.loadUsersTCPInfo(object);
        }
        return serverTCP.users;
    }

    public Vector<User> loadUsersRMIInfo(ObjectFile object) throws ClassNotFoundException {
        //Users
        File f = new File("UsersRMI.dat");

        if (!f.exists()) {
            return this.serverRMI.users;
        }
        try {
            object.openRead("UsersRMI.dat");

            serverRMI.users = (Vector<User>) object.readObject();

            object.closeRead();
        } catch (IOException ioe) {
            System.out.println("Couldn't load users info: " + ioe);
            this.loadUsersRMIInfo(object);
        }
        return serverRMI.users;
    }

    public ConcurrentMap<Integer, Post> loadPostsTCPInfo(ObjectFile object) throws ClassNotFoundException {
        //Posts
        File g = new File("PostsTCP.dat");

        if (!g.exists()) {

            return this.serverTCP.posts;
        }
        try {
            object.openRead("PostsTCP.dat");
            this.serverTCP.posts = (ConcurrentHashMap<Integer, Post>) object.readObject();
            if (!serverTCP.posts.isEmpty()) {
                serverTCP.countID = this.serverTCP.posts.size() - 1;
            }

            object.closeRead();
        } catch (IOException ioe) {
            System.out.println("Couldn't load posts info: " + ioe);
            this.loadPostsTCPInfo(object);

        }
        return this.serverTCP.posts;
    }

    public ConcurrentMap<Integer, Post> loadPostsRMIInfo(ObjectFile object) throws ClassNotFoundException {
        //Posts
        File g = new File("PostsRMI.dat");

        if (!g.exists()) {

            return this.serverRMI.getPosts();
        }
        try {
            object.openRead("PostsRMI.dat");
            this.serverRMI.setPosts((ConcurrentHashMap<Integer, Post>) object.readObject());
            if (!serverRMI.getPosts().isEmpty()) {
                serverRMI.countID = this.serverRMI.getPosts().size() - 1;
            }

            object.closeRead();
        } catch (IOException ioe) {
            System.out.println("Couldn't load posts info: " + ioe);
            this.loadPostsRMIInfo(object);
        }
        return this.serverRMI.getPosts();
    }

    public Vector<Message> loadMessagesTCPInfo(ObjectFile object) throws ClassNotFoundException {
        //Messages
        File m = new File("MessagesTCP.dat");
        if (!m.exists()) {

            return this.serverTCP.messages;
        }
        try {
            object.openRead("MessagesTCP.dat");
            this.serverTCP.messages = (Vector<Message>) object.readObject();
            object.closeRead();
        } catch (IOException ioe) {
            System.out.println("Couldn't load messages info: " + ioe);
            this.loadMessagesTCPInfo(object);

        }
        return this.serverTCP.messages;
    }

    public Vector<Message> loadMessagesRMIInfo(ObjectFile object) throws ClassNotFoundException {
        //Messages
        File m = new File("MessagesRMI.dat");
        if (!m.exists()) {

            return this.serverRMI.messages;
        }
        try {
            object.openRead("MessagesRMI.dat");
            this.serverRMI.messages = (Vector<Message>) object.readObject();
            object.closeRead();
        } catch (IOException ioe) {
            System.out.println("Couldn't load messages info: " + ioe);
            this.loadMessagesRMIInfo(object);
        }
        return this.serverRMI.messages;
    }

    public Vector<DelayedPost> loadDelayedPostsTCPInfo(ObjectFile object) throws ClassNotFoundException {
        //Delayed Posts
        File f = new File("DelayedPostsTCP.dat");

        if (!f.exists()) {
            serverTCP.delayedPosts.setToBeSent(this.serverTCP.delayedPosts.getToBeSent());
            return this.serverTCP.delayedPosts.getToBeSent();
        }
        try {
            object.openRead("DelayedPostsTCP.dat");
            serverTCP.delayedPosts.setToBeSent((Vector<DelayedPost>) object.readObject());
            object.closeRead();
        } catch (IOException ioe) {
            System.out.println("Couldn't load delayed posts info: " + ioe);
            this.loadDelayedPostsTCPInfo(object);
        }
        return serverTCP.delayedPosts.getToBeSent();
    }

    public Vector<DelayedPost> loadDelayedPostsRMIInfo(ObjectFile object) throws ClassNotFoundException {
        //Delayed Posts
        File f = new File("DelayedPostsRMI.dat");

        if (!f.exists()) {
            serverRMI.delayedPosts.setToBeSent(this.serverRMI.delayedPosts.getToBeSent());
            return this.serverRMI.delayedPosts.getToBeSent();
        }
        try {
            object.openRead("DelayedPostsRMI.dat");
            serverRMI.delayedPosts.setToBeSent((Vector<DelayedPost>) object.readObject());
            object.closeRead();
        } catch (IOException ioe) {
            System.out.println("Couldn't load delayed posts info: " + ioe);
            this.loadDelayedPostsRMIInfo(object);
        }
        return serverRMI.delayedPosts.getToBeSent();
    }

    @SuppressWarnings("unchecked")
    public void saveInfo(ObjectFile objectU, ObjectFile objectP, ObjectFile objectM, ObjectFile objectD, ObjectFile objectUR, ObjectFile objectPR, ObjectFile objectMR, ObjectFile objectDR) {
        //Users
        try {
            objectU.openWrite("UsersTCP.dat");
            objectU.writeObject(this.serverTCP.users);
            objectU.closeWrite();
        } catch (IOException ioe) {
            System.out.println("Couldn't save info: " + ioe);
            this.saveInfo(objectU, objectP, objectM, objectD, objectUR, objectPR, objectMR, objectDR);

        }

        //Posts
        try {
            objectP.openWrite("PostsTCP.dat");
            objectP.writeObject(this.serverTCP.posts);
            objectP.closeWrite();
        } catch (IOException ioe) {
            System.out.println("Couldn't save info: " + ioe);
            this.saveInfo(objectU, objectP, objectM, objectD, objectUR, objectPR, objectMR, objectDR);

        }

        //Messages
        try {
            objectM.openWrite("MessagesTCP.dat");
            objectM.writeObject(this.serverTCP.messages);
            objectM.closeWrite();
        } catch (IOException ioe) {
            System.out.println("Couldn't save info: " + ioe);
            this.saveInfo(objectU, objectP, objectM, objectD, objectUR, objectPR, objectMR, objectDR);

        }


        //DelayedPosts
        try {
            objectD.openWrite("DelayedPostsTCP.dat");
            objectD.writeObject(this.serverTCP.delayedPosts.getToBeSent());
            objectD.closeWrite();
        } catch (IOException ioe) {
            System.out.println("Couldn't save info: " + ioe);
            this.saveInfo(objectU, objectP, objectM, objectD, objectUR, objectPR, objectMR, objectDR);

        }

        //Users
        try {
            objectUR.openWrite("UsersRMI.dat");
            objectUR.writeObject(this.serverRMI.users);
            objectUR.closeWrite();
        } catch (IOException ioe) {
            System.out.println("Couldn't save info: " + ioe);
            this.saveInfo(objectU, objectP, objectM, objectD, objectUR, objectPR, objectMR, objectDR);

        }

        //Posts
        try {
            objectPR.openWrite("PostsRMI.dat");
            objectPR.writeObject(this.serverRMI.getPosts());
            objectPR.closeWrite();
        } catch (IOException ioe) {
            System.out.println("Couldn't save info: " + ioe);
            this.saveInfo(objectU, objectP, objectM, objectD, objectUR, objectPR, objectMR, objectDR);

        }

        //Messages
        try {
            objectMR.openWrite("MessagesRMI.dat");
            objectMR.writeObject(this.serverRMI.messages);
            objectMR.closeWrite();
        } catch (IOException ioe) {
            System.out.println("Couldn't save info: " + ioe);
            this.saveInfo(objectU, objectP, objectM, objectD, objectUR, objectPR, objectMR, objectDR);

        }


        //DelayedPosts
        try {
            objectDR.openWrite("DelayedPostsRMI.dat");
            objectDR.writeObject(this.serverRMI.delayedPosts.getToBeSent());
            objectDR.closeWrite();
        } catch (IOException ioe) {
            System.out.println("Couldn't save info: " + ioe);
            this.saveInfo(objectU, objectP, objectM, objectD, objectUR, objectPR, objectMR, objectDR);

        }
    }

    public Server(int serverPort) {
        this.serverPort = serverPort;
        this.hostname = "localhost";
        this.waitMainServer();
        this.startServer();

    }

    public static void main(String args[]) {
        if (args.length == 1) {
            if (args[0].equals("1") || args[0].equals("2")) {
                new Server(6000 + Integer.parseInt(args[0]) - 1);
            } else {
                System.out.println("Usage: java Server priority[1, 2].");
            }
        } else {
            System.out.println("Usage: java Server priority[1, 2].");
        }
    }
}
