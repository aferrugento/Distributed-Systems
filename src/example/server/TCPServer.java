package example.server;


import java.util.*;
import java.util.concurrent.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {

    public Vector< TCPServerThread> clients;
    private ServerSocket server = null;
    private Thread thread = null;
    private int clientCount = 0;
    private int serverPort;
    public int countID;
    public static ExecutorService pool = Executors.newCachedThreadPool();
    public ConcurrentMap<Integer, Post> posts;
    public Vector<Message> messages;
    public Vector<User> users;
    public Delay delayedPosts;

    public TCPServer(int port) {
        this.serverPort = port;

        System.out.println("Starting TCP Server.");
        try {
            System.out.println("Binding to port " + serverPort + ", please wait...");
            server = new ServerSocket(serverPort);
            System.out.println("Server started: " + server);

            start();
        } catch (IOException ioe) {
            System.out.println("Can not bind to port " + serverPort + ": " + ioe.getMessage() + ".");
        }
    }

    public void run() {

        while (thread != null) {
            try {
                System.out.println("Waiting for a client...");
                addThread(server.accept());
            } catch (IOException ioe) {
                System.out.println("Server accept error: " + ioe);
                stop();
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    private int findClient(int ID) {
        for (int i = 0; i < clientCount; i++) {
            if (clients.get(i).getID() == ID) {
                return i;
            }
        }
        return -1;
    }

    public void handle(User sender, String input, Boolean notification) {

        {
            for (int i = 0; i < clientCount; i++) {
                if (clients.get(i).getUser() != sender) {
                    if (notification) {
                        clients.get(i).send(sender.getName() + " " + input);
                    } else {
                        clients.get(i).send(sender.getName() + " made a new post."); //porque esta na checklist
                        clients.get(i).send(sender.getName() + ": " + input);
                    }
                }
            }

            if (!notification) {
                countID++;
                posts.put(countID, new Post(sender, input, null));
            }
        }
    }

    public TCPServerThread getThread(User u) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getUser().getName().equals(u.getName())) {
                return clients.get(i);
            }
        }
        return null;
    }

    public void handleMessages(Message mess) {

        if (clients.contains(this.getThread(mess.getReceiver()))) {
            mess.setSeen(true);
            this.getThread(mess.getReceiver()).send("You have a new message from " + mess.getAuthor().getName() + "."); //porque esta na checklist
            this.getThread(mess.getReceiver()).send(mess.getAuthor().getName() + ": " + mess.format());
            messages.add(mess);
        } else {
            messages.add(mess);

        }

    }

    public synchronized void remove(int ID) {
        int pos = findClient(ID);
        TCPServerThread toTerminate = clients.get(pos);
        System.out.println("Removing client thread " + ID + " at " + pos);

        clients.remove(pos);

        clientCount--;

        try {
            toTerminate.close();
        } catch (IOException ioe) {
            System.out.println("Error closing thread: " + ioe);
        }

        toTerminate.stop();
    }

    private void addThread(Socket socket) {

        clients.add(new TCPServerThread(this, socket));

        try {
            clients.get(clientCount).open();
            pool.submit(clients.get(clientCount));


            clientCount++;
        } catch (IOException ioe) {
            System.out.println("Error opening thread: " + ioe);
        }

    }
}