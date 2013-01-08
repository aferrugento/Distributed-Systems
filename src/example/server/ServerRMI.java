package example.server;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.*;

import javax.imageio.ImageIO;



public class ServerRMI extends UnicastRemoteObject implements InterfaceServerRMI {

    public Vector<ClientRMIConnection> clients;
    private ConcurrentHashMap<Integer, Post> posts;
    public Vector<Message> messages;
    public Vector<User> users;
    public DelayR delayedPosts;
    public int countID;
    private static int imageName=0;

    public ServerRMI() throws RemoteException {
        super();
       
    }

    public void logout(User user) {
        this.remove(user);
        this.handle(user, "just disconnected.", true, null);

    }

    public synchronized void remove(User user) {
        for (int i = 0; i < this.clients.size(); i++) {
            if (this.clients.get(i).getUser().getName().equals(user.getName())) {
                clients.remove(i);
                break;
            }
        }
    }

    public String menu1() {
        return "\n				.:SAYMORE:.				\n\n\nWelcome to SAYMORE! Choose your option:\n\n  (1) Sign up\n  (2) Sign in\n\n:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.";
    }

    public String menu2() {
        return "\n  (1)  Make a post\n  (2)  Make a delayed post\n  (3)  Edit a post\n  (4)  Delete a post\n  (5)  Reply to post\n  (6)  Edit reply to post\n  (7)  Delete reply to post\n  (8)  Send direct message\n  (9)  See messages\n  (10) See posts\n  (11) See replies to a post \n  (12) Show online users\n  (13) Sign out\n  (14) Create post with Facebook\n  (15) Post an image\n\n:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.";
    }

    public void handle(User sender, String input, Boolean notification, String facebook) {

        for (int i = 0; i < clients.size(); i++) {
            if (!clients.get(i).getUser().getName().equals(sender.getName())) {
                if (notification) {
                    clients.get(i).send(sender.getName() + " " + input, clients.get(i).getUser());
                } else {
                    clients.get(i).send(sender.getName() + " made a new post.",  clients.get(i).getUser()); //porque esta na checklist
                    clients.get(i).send(sender.getName() + ": " + input,  clients.get(i).getUser());
                }
            }
        }
        if (!notification) {
            countID++;
            posts.put(countID, new Post(sender, input, facebook));
        }

    }

    public ClientRMIConnection getClient(User u) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getUser().getName().equals(u.getName())) {
                return clients.get(i);
            }
        }
        return null;
    }

    public void handleMessages(Message mess) {

        if (clients.contains(this.getClient(mess.getReceiver()))) {
            mess.setSeen(true);
            this.getClient(mess.getReceiver()).send("You have a new message from " + mess.getAuthor().getName() + ".", mess.getReceiver()); //porque esta na checklist
            this.getClient(mess.getReceiver()).send(mess.getAuthor().getName() + ": " + mess.format(), mess.getReceiver());
            messages.add(mess);
        } else {
            messages.add(mess);
        }
    }

    public String newMessages(User user) {

        int count = 0;
        for (int i = 0; i < this.messages.size(); i++) {
            if (this.messages.get(i).getReceiver().getName().equals(user.getName()) && (!(this.messages.get(i).getSeen()))) {
                count++;
            }
        }
        return "You have " + count + " new messages!";
    }

    public User searchUser(String name) {
        int i;
        for (i = 0; i < this.users.size(); i++) {
            if (this.users.get(i).getName().equals(name)) {
                return this.users.get(i);
            }
        }
        return null;
    }

    public void loginRequest(User user, InterfaceClientRMI remote, boolean web) {
        ClientRMIConnection client = new ClientRMIConnection(user, remote, web);
        this.clients.add(client);
       System.out.println("Entrei no loginRequest");
//        ChatWebSocketServlet auxM = new ChatWebSocketServlet() ;
//        ChatMessageInbound aux= auxM.new ChatMessageInbound();
//        aux.broadcast(user.getName() + " is now connected!");
    }

    public void register(User user, InterfaceClientRMI remote, boolean web) {

        this.users.add(user);
        ClientRMIConnection client = new ClientRMIConnection(user, remote, web);
        this.clients.add(client);
        
        System.out.println("Entrei no register");
//        ChatWebSocketServlet auxM = new ChatWebSocketServlet() ;
//        ChatMessageInbound aux= auxM.new ChatMessageInbound();
//        aux.broadcast(user.getName() + " is now connected!");
        
        //gerar evento no javascript?
    }

    public void listMessages(User user) {

        for (int i = 0; i < this.messages.size(); i++) {
            if (this.messages.get(i).getReceiver().getName().equals(user.getName())) {
                this.getClient(user).send("By: " + this.messages.get(i).getAuthor().getName() + " " + this.messages.get(i).format(), user);
            }
        }
    }

    public void listPosts(User user) {
        int i = 0, outro_counter = 0;
        while(outro_counter < this.posts.size())
        {
            if(this.posts.containsKey(i))
            {
                outro_counter++;
                this.getClient(user).send("[ID=" + i + "] " + this.posts.get(i).getAuthor().getName() + ": " + this.posts.get(i).getPost(), user);
            }
            i++;
        }
    }

    public void listOnline(User user) {
        for (int i = 0; i < this.clients.size(); i++) {
            this.getClient(user).send(this.clients.get(i).getUser().getName(), user);
        }

    }

    public void listComments(int post, User user) {

        for (int i = 0; i < this.posts.get(post).getList_comments().size(); i++) {
            this.getClient(user).send("[ID=" + i + "] " + this.posts.get(post).getList_comments().get(i).getAuthor().getName() + ": " + this.posts.get(post).getList_comments().get(i).getComment(), user);
        }
    }

    public void delayPost(DelayedPost delayed) {
        this.delayedPosts.awake();
        this.delayedPosts.addNew(delayed);
    }

    public void deleteReply(int post, int reply) {
        this.posts.get(post).getList_comments().remove(reply);
    }

    public void editReply(int post, int reply, Reply newReply) {

        this.posts.get(post).getList_comments().set(reply, newReply);

    }

    public void deletePost(int postID) {
        this.posts.remove(postID);
    }

    public void editPost(int chosenPostID, String newPost) {
        this.posts.get(chosenPostID).setPost(newPost);
    }

    public void reply(int chosenPostID, Reply reply) {
        this.posts.get(chosenPostID).getList_comments().add(reply);
    }

    public ConcurrentHashMap<Integer, Post> getPosts() {
        return this.posts;
    }

    public void setPosts(ConcurrentHashMap<Integer, Post> posts) {
        this.posts = posts;
    }
    
    public Vector<String> getClientsNames()
    {
    	Vector<String> list= new Vector <String>();
    	for(int i =0; i<this.clients.size(); i++)
    	{
    		list.add(clients.get(i).getUser().getName());
    	}
    	
    	return list;
    }
    
    public void image(byte[] rawImage, User sender) {  
    	
        
        for (int i = 0; i < clients.size(); i++) {
              clients.get(i).sendImage(rawImage, sender, Integer.toString(imageName));
  
        }
        
		BufferedImage img=null;
        try {
			 img = javax.imageio.ImageIO.read(new ByteArrayInputStream(rawImage));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
		 File outputfile = new File("C:\\Users\\mariana\\workspace\\_SD2\\WebContent\\userImages\\"+sender.getName()+Integer.toString(imageName++) +".bmp");
		 try {
			ImageIO.write(img, "bmp", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
    }
    
    public Vector <String> getMessages(User user) {
    	Vector <String> messageList = new Vector <String>();
        for (int i = 0; i < this.messages.size(); i++) {
            if (this.messages.get(i).getReceiver().getName().equals(user.getName())) {
                messageList.add("By: " + this.messages.get(i).getAuthor().getName() + "<br>" + this.messages.get(i).format2());
            }
        }
        return messageList;
    }
}
