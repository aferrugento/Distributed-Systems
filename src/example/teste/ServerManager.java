package example.teste;

import java.rmi.registry.LocateRegistry;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Vector;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import example.server.*;


public class ServerManager {

	private DataInputStream console = null;
	private InterfaceServerRMI serverRemote;
	private RMIReceiver rmiReceiver;
	private String hostname;
	private int port;
	private Vector<User> webUsers;
	public static HashMap<String, String> imagesURL ;

	public ServerManager() {

		this.hostname = "localhost";
		this.port = 7000;
		console = new DataInputStream(System.in);
		webUsers = new Vector<User>();
		imagesURL= new HashMap<String, String>();

		try {
			this.rmiReceiver = new RMIReceiver();

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (!this.Connect()) {
			System.out.println("OI2");
			System.exit(1);
		}

	}

	public void logout(User user) {
		try {
			this.serverRemote.logout(user);
		} catch (RemoteException e) {
			this.reconnect();
		}
		for (int i = 0; i < this.webUsers.size(); i++) {
			if (this.webUsers.get(i).getName().equals(user.getName())) {
				this.webUsers.remove(i);
				break;
			}
		}
	}

	public void post(String post, User user) {
		try {
			this.serverRemote.handle(user, post, false, null);
		} catch (IOException ioe) {
			this.reconnect();
		}
	}

	public void postFacebook(String post, User user) {
		FacebookRestClient rest = new FacebookRestClient();

		String facebook = (String) rest.post(post);

		try {
			this.serverRemote.handle(user, post, false, facebook);
		} catch (IOException ioe) {
			this.reconnect();
		}

	}

	public void addPostsF(HashMap <String, String> posts)
	{

		User f= new User ("Facebook", "facebook");
		
		HashMap <String, String> temp = new HashMap<String, String>() ;
		ConcurrentHashMap<Integer, Post> ourPosts = null;
		String postID;
		
		for(String key : posts.keySet())
		{
			temp.put(key, posts.get(key));
		}
		
		try {
			 ourPosts = this.serverRemote.getPosts();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Post post : ourPosts.values())
		{
			for(String key : posts.keySet())
			{
				if((postID=post.getFacebook())!= null )
				{
					if(postID.equals(key))
					{
						temp.remove(key);
						
					}
				}
				
			}
		}
		
		for(String keyt : temp.keySet())
		{
			try {
				this.serverRemote.handle(f, temp.get(keyt), false, keyt);
			} catch (IOException ioe) {
				this.reconnect();
			}
		}	
		
	}

	public void editPost(String postID, String newPostString, User user) {

		int aux = Integer.parseInt(postID, 10);

		try {
			this.serverRemote.editPost(aux, newPostString);

			this.serverRemote.handle(user, "edited the post number " + postID,
					true, null);
		} catch (RemoteException e) {
			this.reconnect();
			e.printStackTrace();
		}
	}

	public void deletePost(String postID) {

		int aux = Integer.parseInt(postID, 10);
		Post chosenPost;

		try {
			if ((chosenPost = this.serverRemote.getPosts().get(aux))
					.getFacebook() != null) {
				FacebookRestClient rest = new FacebookRestClient();
				rest.deletePost(chosenPost);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.serverRemote.deletePost(aux);
		} catch (RemoteException e) {
			this.reconnect();
		}

	}


	
	
	public static void addImage(){
		
		imagesURL.clear();
		File images[]=new File("C:\\Users\\mariana\\workspace\\_SD2\\WebContent\\userImages\\").listFiles();
		System.out.println(images.length);
		
		for(int i=0 ; i<images.length-1; i++)
		{
			if(images[i].toString().endsWith(".bmp"))
			{
				String pathName=images[i].toString();
				int ind = pathName.indexOf("userImages");
				String urlTemp = images[i].toString().substring(ind);
				System.out.println(urlTemp);
				String name= urlTemp.substring(10);
				name.indexOf(".");
				imagesURL.put(urlTemp, name.substring(1, name.indexOf(".")-1));
			}
		}
		
 		
	}
	

	public void image(String path, User user) {

		BufferedImage image=null;
		
		File img= new File(path);
		
		try {

			image = ImageIO.read(img);
		} catch (IOException e) {

		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    try {
			javax.imageio.ImageIO.write(image, "bmp", baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	    try {
			this.serverRemote.image(baos.toByteArray(), user);
		} catch (IOException re) {
			this.reconnect();
		}  

	}

	public void reply(String reply, String postID, User user) {

		int aux = Integer.parseInt(postID, 10);
		Post chosenPost;
		try {
			this.serverRemote.reply(aux, new Reply(reply, user));
		} catch (IOException re) {
			this.reconnect();
		}

		try {
			this.serverRemote.handle(user, "made a reply in post number"
					+ postID, true, null);
		} catch (IOException re) {
			this.reconnect();
		}

		try {
			if ((chosenPost = this.serverRemote.getPosts().get(aux))
					.getFacebook() != null) {
				FacebookRestClient rest = new FacebookRestClient();
				System.out.println("-->" + rest.addComment(reply, chosenPost));
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void editReply(String postID, String replyID, String reply, User user) {

		int auxP = Integer.parseInt(postID, 10);
		int auxR = Integer.parseInt(replyID, 10);

		try {
			this.serverRemote.editReply(auxP, auxR, new Reply(reply, user));
		} catch (IOException ioe) {
			this.reconnect();
		}
		try {
			this.serverRemote.handle(user, "edited a reply in post number "
					+ postID, true, null);
		} catch (IOException ioe) {
			this.reconnect();
		}
	}

	public void deleteReply(String postID, String replyID) {

		int auxP = Integer.parseInt(postID, 10);
		int auxR = Integer.parseInt(replyID, 10);

		try {
			this.serverRemote.deleteReply(auxP, auxR);
		} catch (RemoteException e) {
			this.reconnect();
		}

	}

	public void message(String title, String subject, String mess, User from,
			String destName) {

		User dest;

		try {
			dest = this.serverRemote.searchUser(destName);
			this.serverRemote.handleMessages(new Message(title, subject, mess,
					from, dest, false)); // notificacao na propria funcao

		} catch (IOException ioe) {
			this.reconnect();
		}
	}

	public Vector<String> listMessages(String userName) {
		try {
			User user = this.serverRemote.searchUser(userName);
			return this.serverRemote.getMessages(user);
		} catch (RemoteException e) {
			this.reconnect();
		}
		return null;
	}

	public ConcurrentHashMap<Integer, Post> listPosts() {
		try {
			return this.serverRemote.getPosts();
		} catch (RemoteException e) {
			this.reconnect();
			return null;
		}
	}

	//
	public Vector<Reply> listComments(int postID) {

		try {
			return this.serverRemote.getPosts().get(postID).getList_comments();
		} catch (RemoteException e) {
			this.reconnect();
			return null;
		}
	}

	// public void listOnline() {
	// try {
	// this.serverRemote.listOnline(this.user);
	// } catch (RemoteException e) {
	// this.reconnect();
	// }
	// }
	//
	public User register(String tempName, String tempPass) {

		User temp = null;
		try {

			if (this.serverRemote.searchUser(tempName) != null) {
				return null;
			}

			this.serverRemote.register((temp = new User(tempName, tempPass)),
					rmiReceiver, true);

			System.out.println("Registration and login done!");

			this.webUsers.add(temp);

			this.serverRemote.handle(temp, "is now connected!", true, null);
		} catch (IOException e) {
			this.reconnect();
		}

		return temp;

	}

	public User loginRequest(String tempName, String tempPass) {
		User temp = null;

		try {

			if ((temp = this.serverRemote.searchUser(tempName)) == null) {

				return null;
			}

			if (!temp.authentication(tempPass)) {

				return null;
			}

			this.serverRemote.loginRequest(temp, rmiReceiver, true);
			this.webUsers.add(temp);
			this.serverRemote.handle(temp, "is now connected!", true, null);

		} catch (IOException e) {
			this.reconnect();
		}

		return temp;
	}

	public boolean Connect() {

		try {

			this.serverRemote = (InterfaceServerRMI) LocateRegistry
					.getRegistry("localhost", 7000).lookup("SayMore");

		} catch (AccessException e) {
			System.out.println("Access Exception caught doing lookup"
					+ e.getCause());
			return false;
		} catch (RemoteException e) {
			System.out.println("Remote Exception caught doing lookup"
					+ e.getCause());
			return false;
		} catch (NotBoundException e) {
			System.out.println("NotBound Exception caught doing lookup"
					+ e.getCause());
			return false;
		}

		/*
		 * if (this.login) { try {
		 * this.serverRemote.loginRequest(webUsers.get(webUsers.size()-1),
		 * rmiReceiver); } catch (RemoteException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } }
		 */

		return true;

	}

	public String onlineList() {
		int i;
		Vector<String> list = new Vector<String>();
		try {
			list = this.serverRemote.getClientsNames();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String listString = "";

		for (i = 0; i < list.size(); i++) {
			listString += list.get(i) + "<br>";
		}

		return listString;

	}

	private void reconnect() {
		System.err.println("Server is down.");
		while (!this.Connect()) {
			try {
				System.err.println("Working on it...");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}
	}

}
