package example.server;


import java.rmi.*;
import java.util.Vector;
import java.util.concurrent.*;

public interface InterfaceServerRMI extends Remote {

    public String menu1() throws RemoteException;

    public String menu2() throws RemoteException;

    public void handle(User sender, String input, Boolean notification, String facebook) throws RemoteException;

    public void handleMessages(Message mess) throws RemoteException;

    public void loginRequest(User user, InterfaceClientRMI remote, boolean web) throws RemoteException;

    public void register(User user, InterfaceClientRMI remote, boolean web) throws RemoteException;

    public String newMessages(User user) throws RemoteException;

    public void delayPost(DelayedPost delayed) throws RemoteException;

    public ConcurrentHashMap<Integer, Post> getPosts() throws RemoteException;

    public void editPost(int chosenPostID, String newPost) throws RemoteException;

    public void deletePost(int postID) throws RemoteException;

    public void reply(int chosenPostID, Reply reply) throws RemoteException;

    public void editReply(int post, int reply, Reply newReply) throws RemoteException;

    public void deleteReply(int post, int reply) throws RemoteException;

    public User searchUser(String name) throws RemoteException;

    public void listMessages(User user) throws RemoteException;

    public void listPosts(User user) throws RemoteException;

    public void listComments(int post, User user) throws RemoteException;

    public void listOnline(User user) throws RemoteException;

    public void logout(User user) throws RemoteException;
    
    public Vector<String> getClientsNames() throws RemoteException;
    
    public Vector <String> getMessages(User user) throws RemoteException;
    
    public void image(byte[] rawImage, User user) throws RemoteException;
}