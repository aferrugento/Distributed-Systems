package example.server;


import java.rmi.registry.LocateRegistry;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;

import example.teste.FacebookRestClient;
import example.teste.ServerManager;

public class ClientRMI {

    private DataInputStream console = null;
    private InterfaceServerRMI serverRemote;
    private RMIReceiver rmiReceiver;
    private String hostname;
    private int port;
    private User user;
    private boolean login;

    public ClientRMI(String hostname) {
        this.hostname = hostname;
        this.port = 7000;
        this.login = false;
        console = new DataInputStream(System.in);
        try {
            this.rmiReceiver = new RMIReceiver();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (!this.Connect()) {
            System.exit(1);
        }
    }

    public void iniciate() {
        if (this.login) {
            menu2();
        } else {
            menu1();
            try {
                System.out.println(this.serverRemote.newMessages(this.user));
            } catch (RemoteException e) {
                this.reconnect();
            }
            if (this.login) {
                menu2();
            }
        }
    }

    public void menu1() {
        String choice = null;
        try {
            System.out.println(this.serverRemote.menu1());
        } catch (RemoteException e) {
            this.reconnect();
        }

        try {
            choice = console.readLine();
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
        }


        if (choice.equals("1")) {
            register();
            return;
        } else if (choice.equals("2")) {
            loginRequest();
            return;
        } else {
            System.out.println("Invalid choice. Choose 1 or 2.");
            menu1();
            return;

        }
    }

    public void menu2() {
        int choiceNumber = 0;
        try {
            System.out.println(this.serverRemote.menu2());
        } catch (RemoteException e) {
            this.reconnect();
        }
        String choice = null;

        try {
            choice = console.readLine();
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            menu2();
        }
        try {
            choiceNumber = Integer.parseInt(choice, 10);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid choice. Choose between 1 and 14.");
            menu2();
        }

        switch (Integer.parseInt(choice, 10)) {
            case 1:
                post();
                menu2();
                break;
            case 2:
                delayPost();
                menu2();
                break;
            case 3:
                editPost();
                menu2();
                break;
            case 4:
                deletePost();
                menu2();
                break;
            case 5:
                reply();
                menu2();
                break;
            case 6:
                editReply();
                menu2();
                break;
            case 7:
                deleteReply();
                menu2();
                break;
            case 8:
                message();
                menu2();
                break;
            case 9:
                listMessages();
                menu2();
                break;
            case 10:
                listPosts();
                menu2();
                break;
            case 11:
                listComments();
                menu2();
                break;
            case 12:
                listOnline();
                menu2();
                break;
            case 13:
                logout();
                break;
            case 14:
            	postFacebook();
            case 15:
            	image();

            default:
                System.out.println("Invalid choice. Choose between 1 and 13.");
                menu2();
                break;
        }
    }

    public void logout() {
        try {
            this.serverRemote.logout(this.user);
        } catch (RemoteException e) {
            this.reconnect();
        }
        System.exit(1);
    }

    public void post() {
        System.out.println("Type the post:");

        try {
            this.serverRemote.handle(this.user, console.readLine(), false, null);
        } catch (IOException ioe) {
            this.reconnect();
        }
    }

    public void editPost() {
        Post chosenPost = null;
        String postID = null;
        String newPost = null;
        int aux = 0;
        System.out.println("Enter the post ID: ");

        try {
            postID = console.readLine();
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            editPost();
        }
        try {
            aux = Integer.parseInt(postID, 10);
            if ((!this.serverRemote.getPosts().containsKey(aux)) || (!(chosenPost = this.serverRemote.getPosts().get(aux)).getAuthor().getName().equals(this.user.getName()))) {
                System.out.println("Invalid ID. Try again[1] or get back to menu[2]?");

                if (console.readLine().equals("1")) {
                    editPost();
                    return;
                } else {
                    menu2();
                    return;
                }

            }
        } catch (IOException ioe) {
            this.reconnect();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID.");
            editPost();
        }

        try {
            this.serverRemote.getPosts().get(Integer.parseInt(postID, 10));
        } catch (RemoteException ioe) {
            this.reconnect();
        }
        System.out.println("Is this post: " + chosenPost.getPost() + "? [y/n]");
        try {
            if (!console.readLine().equalsIgnoreCase("y")) {
                System.out.println("Try again[1] or get back to menu[2]? ");
                if (console.readLine().equals("1")) {
                    editPost();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            editPost();
        }

        System.out.println("Type the new post: ");
        try {
            newPost = console.readLine();
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            editPost();
        }

        try {
            this.serverRemote.editPost(aux, newPost);
            this.serverRemote.handle(this.user, "edited the post number " + postID, true, null);
        } catch (RemoteException e) {
            this.reconnect();
        }
    }

    public void deletePost() {
        Post chosenPost = null;
        String postID = null;
        int aux = 0;
        System.out.println("Enter the post ID: ");
        try {
            postID = console.readLine();
            aux = Integer.parseInt(postID, 10);
            if ((!this.serverRemote.getPosts().containsKey(aux)) || (!(chosenPost = this.serverRemote.getPosts().get(aux)).getAuthor().getName().equals(this.user.getName()))) {
                System.out.println("Invalid ID. Try again[1] or get back to menu[2]?");
                if (console.readLine().equals("1")) {
                    deletePost();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.reconnect();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID.");
            deletePost();
        }


        try {
            this.serverRemote.getPosts().get(Integer.parseInt(postID, 10));
        } catch (RemoteException re) {
            this.reconnect();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID.");
            deletePost();
        }

        System.out.println("Is this post: " + chosenPost.getPost() + "? [y/n]");
        try {
            if (!console.readLine().equalsIgnoreCase("y")) {
                System.out.println("Try again[1] or get back to menu[2]? ");
                if (console.readLine().equals("1")) {
                    deletePost();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            deletePost();
        }
        
		try {
			if ((chosenPost = this.serverRemote.getPosts().get(aux))
					.getFacebook() != null) {
				FacebookRestClientRMI rest = new FacebookRestClientRMI(this);
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

    public void delayPost() {
        String day = null;
        String month = null;
        String hours = null;
        String minutes = null;
        String input = null;
        Calendar date;

        try {
            System.out.println("Type the post:");
            input = console.readLine();

            System.out.println("Enter the time to publish the post.\n month: ");
            month = console.readLine();

            System.out.println(" day: ");
            day = console.readLine();

            System.out.println(" hours: ");
            hours = console.readLine();

            System.out.println(" minutes: ");
            minutes = console.readLine();
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            delayPost();
        }
        Calendar current = Calendar.getInstance();
        date = Calendar.getInstance();
        try {
            date.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
            date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
            date.set(Calendar.MINUTE, Integer.parseInt(minutes));
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID.");
            delayPost();
        }

        if (date.before(current)) {
            try {

                System.out.println("Invalid date. Try again[1] or get back to menu[2]?");
                if (console.readLine().equals("1")) {
                    delayPost();
                    return;
                } else {
                    menu2();
                    return;
                }
            } catch (IOException ioe) {
                System.out.println("Error reading from console.");
                delayPost();
            }


        }
        try {
            this.serverRemote.delayPost(new DelayedPost(date, this.user, input, null));

            this.serverRemote.handle(this.user, "made a new delayed post.", true, null);
        } catch (RemoteException e) {
            this.reconnect();
        }
    }

    public void reply() {
        Post chosenPost = null;
        String postID = null;

        int aux = 0;
        System.out.println("Enter the post ID: ");
        try {
            postID = console.readLine();
            aux = Integer.parseInt(postID, 10);
            if (!this.serverRemote.getPosts().containsKey(aux)) {
                System.out.println("Invalid ID. Try again[1] or get back to menu[2]?");
                if (console.readLine().equals("1")) {
                    reply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.reconnect();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID.");
            reply();
        }

        try {
            chosenPost = this.serverRemote.getPosts().get(Integer.parseInt(postID, 10));
        } catch (RemoteException re) {
            this.reconnect();
        }
        System.out.println("Is this post: " + chosenPost.getPost() + "? [y/n]");
        try {
            if (!console.readLine().equalsIgnoreCase("y")) {
                System.out.println("Try again[1] or get back to menu[2]? ");
                if (console.readLine().equals("1")) {
                    reply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            reply();
        }

        System.out.println("Type the reply: ");
        try {
            this.serverRemote.reply(aux, new Reply(console.readLine(), this.user));
        } catch (IOException re) {
            this.reconnect();
        }

        try {
            this.serverRemote.handle(this.user, "made a reply in post number " + postID, true, null);
        } catch (IOException re) {
            this.reconnect();
        }
        
        
        try {
			if ((chosenPost = this.serverRemote.getPosts().get(aux))
					.getFacebook() != null) {
				FacebookRestClientRMI rest = new FacebookRestClientRMI(this);
			
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void editReply() {
        Post chosenPost = null;
        Reply chosenReply = null;
        String postID = null;
        String replyID = null;
        int auxP = 0;
        int auxR = 0;
        System.out.println("Enter the post ID: ");

        try {
            postID = console.readLine();
            auxP = Integer.parseInt(postID, 10);
            if (!this.serverRemote.getPosts().containsKey(auxP)) {
                System.out.println("Invalid ID. Try again[1] or get back to menu[2]?");
                if (console.readLine().equals("1")) {
                    editReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.reconnect();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID.");
            editReply();
        }

        try {
            chosenPost = this.serverRemote.getPosts().get(auxP);
        } catch (RemoteException re) {
            this.reconnect();
        }
        System.out.println("Is this post: " + chosenPost.getPost() + "? [y/n]");

        try {
            if (!console.readLine().equalsIgnoreCase("y")) {
                System.out.println("Try again[1] or get back to menu[2]? ");
                if (console.readLine().equals("1")) {
                    editReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            editReply();
        }

        System.out.println("Enter the reply ID: ");

        try {
            replyID = console.readLine();
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            editReply();
        }

        try {
            if ((auxR = Integer.parseInt(replyID, 10)) >= chosenPost.getList_comments().size() || auxR < 0 || !((chosenReply = chosenPost.getList_comments().get(auxR)).getAuthor().getName().equals(this.user.getName()))) {
                System.out.println("Invalid ID. Try again[1] or get back to menu[2]?");
                if (console.readLine().equals("1")) {
                    editReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            editReply();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID.");
            editReply();
        }

        System.out.println("Is this reply: " + chosenReply.getComment() + "? [y/n]");

        try {
            if (!console.readLine().equalsIgnoreCase("y")) {
                System.out.println("Try again[1] or get back to menu[2]? ");
                if (console.readLine().equals("1")) {
                    editReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            editReply();
        }

        System.out.println("Type the new reply: ");

        try {
            this.serverRemote.editReply(auxP, auxR, new Reply(console.readLine(), this.user));
        } catch (IOException ioe) {
            this.reconnect();
        }
        try {
            this.serverRemote.handle(this.user, "edited a reply in post number " + postID, true, null);
        } catch (IOException ioe) {
            this.reconnect();
        }
    }

    public void deleteReply() {
        Post chosenPost = null;
        Reply chosenReply = null;
        String postID = null;
        String replyID = null;
        int auxP = 0;
        int auxR = 0;
        System.out.println("Enter the post ID: ");

        try {
            postID = console.readLine();
            auxP = Integer.parseInt(postID, 10);
            if (!this.serverRemote.getPosts().containsKey(auxP)) {
                System.out.println("Invalid ID. Try again[1] or get back to menu[2]?");
                if (console.readLine().equals("1")) {
                    deleteReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.reconnect();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID.");
            deleteReply();
        }
        try {
            chosenPost = this.serverRemote.getPosts().get(auxP);
        } catch (IOException ioe) {
            this.reconnect();
        }
        System.out.println("Is this post: " + chosenPost.getPost() + "? [y/n]");

        try {
            if (!console.readLine().equalsIgnoreCase("y")) {
                System.out.println("Try again[1] or get back to menu[2]? ");
                if (console.readLine().equals("1")) {
                    deleteReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            deleteReply();
        }


        System.out.println("Enter the reply ID: ");
        try {
            replyID = console.readLine();
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            deleteReply();
        }
        try {
            if ((auxR = Integer.parseInt(replyID, 10)) >= chosenPost.getList_comments().size() || auxR < 0 || !((chosenReply = chosenPost.getList_comments().get(auxR)).getAuthor().getName().equals(this.user.getName()))) //VERIFICAR SE O COMENTARIO E DELE
            {
                System.out.println("Invalid ID. Try again[1] or get back to menu[2]?");


                if (console.readLine().equals("1")) {
                    deleteReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            deleteReply();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID.");
            deleteReply();
        }
        System.out.println("Is this reply: " + chosenReply.getComment() + "? [y/n]");

        try {
            if (!console.readLine().equalsIgnoreCase("y")) {
                System.out.println("Try again[1] or get back to menu[2]? ");
                if (console.readLine().equals("1")) {
                    deleteReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            deleteReply();
        }

        try {
            this.serverRemote.deleteReply(auxP, auxR);
        } catch (RemoteException e) {
            this.reconnect();
        }

    }

    public void message() {
        String mess = null;
        String title = null;
        String subject = null;
        String dest = null;
        User chosenUsr = null;

        System.out.println("Enter the username of destination: ");
        try {
            dest = console.readLine();

            if ((chosenUsr = this.serverRemote.searchUser(dest)) == null) {
                System.out.println("Invalid username. Try again[1] or get back to menu[2]?");
                if (console.readLine().equals("1")) {
                    message();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.reconnect();
        }

        System.out.println("Type the title: ");
        try {
            title = console.readLine();
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            message();
        }
        System.out.println("Type the subject: ");
        try {
            subject = console.readLine();
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            message();
        }
        try {
            System.out.println("Type the message: ");
            mess = console.readLine();
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
            message();
        }
        try {
            this.serverRemote.handleMessages(new Message(title, subject, mess, this.user, chosenUsr, false));
        } catch (IOException ioe) {
            this.reconnect();
        }
    }

    public void listMessages() {
        try {
            this.serverRemote.listMessages(this.user);
        } catch (RemoteException e) {
            this.reconnect();
        }
    }

    public void listPosts() {
        try {
            this.serverRemote.listPosts(this.user);
        } catch (RemoteException e) {
            this.reconnect();
        }
    }

    public void listComments() {
        Post chosenPost = null;
        String postID = null;
        int aux = 0;
        System.out.println("Enter the post ID: ");

        try {
            postID = console.readLine();
            aux = Integer.parseInt(postID, 10);
            if (!this.serverRemote.getPosts().containsKey(aux)) {
                System.out.println("Invalid ID. Try again[1] or get back to menu[2]?");
                if (console.readLine().equals("1")) {
                    listComments();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.reconnect();
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID.");
            listComments();
        }
        try {
            chosenPost = this.serverRemote.getPosts().get(aux);
        } catch (IOException ioe) {
            this.reconnect();
        }

        System.out.println("Is this post: " + chosenPost.getPost() + "? [y/n]");

        try {
            if (!console.readLine().equalsIgnoreCase("y")) {
                System.out.println("Try again[1] or get back to menu[2]? ");
                if (console.readLine().equals("1")) {
                    listComments();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

            this.serverRemote.listComments(aux, this.user);
        } catch (IOException e) {
            this.reconnect();
        }
    }

    public void listOnline() {
        try {
            this.serverRemote.listOnline(this.user);
        } catch (RemoteException e) {
            this.reconnect();
        }
    }

    public void register() {
        String tempName = null;
        String tempPass = null;

        System.out.println("Enter a username: ");
        try {
            tempName = console.readLine();

            if (this.serverRemote.searchUser(tempName) != null) {
                System.out.println("That username has already being used. Try again[1] or get back to menu[2]? ");
                if (console.readLine().equals("1")) {
                    register();
                    return;
                } else {
                    menu1();
                    return;
                }
            } else {

                System.out.println("Enter a password: ");

                tempPass = console.readLine();

            }


            this.serverRemote.register(this.user = new User(tempName, tempPass), rmiReceiver, false);

            System.out.println("Registration and login done!");

            this.serverRemote.handle(this.user, "is now connected!", true, null);
        } catch (IOException e) {
            this.reconnect();
        }
        this.login = true;

    }

    public void loginRequest() {
        User temp = null;
        String tempName = null;
        String tempPass = null;

        System.out.println("Enter your username: ");
        try {
            tempName = console.readLine();

            if ((temp = this.serverRemote.searchUser(tempName)) == null) {
                System.out.println("That username doesn't exist. Try again[1] or get back to menu[2]? ");
                if (console.readLine().equals("1")) {
                    loginRequest();
                    return;
                } else {
                    menu1();
                    return;
                }


            } else {
                System.out.println("Enter your password: ");
                tempPass = console.readLine();
                if (!temp.authentication(tempPass)) {
                    System.out.println("Wrong password or username. ");
                    loginRequest();
                    return;
                }
                this.user = temp;
                
                

                
                this.serverRemote.loginRequest(temp, rmiReceiver, false);
                this.login = true;

             
                
                this.serverRemote.handle(this.user, "is now connected!", true, null);
            }
        } catch (IOException e) {
        	System.out.println("Entrei onde nao devia");
            this.reconnect();
        }
    }

	public void image() {

		BufferedImage image=null;
		System.out.println("Enter the image path: ");
        String path=null;
		try {
			path = console.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
			this.serverRemote.image(baos.toByteArray(), this.user);
		} catch (IOException re) {
			this.reconnect();
		}  

	}
	
	
	
	
	public void postFacebook() {
		
		System.out.println("Type the post:");

        User user = this.user;
        String post= null;
		try {
			post = console.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		FacebookRestClientRMI rest = new FacebookRestClientRMI(this);

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
    
    
    public boolean Connect() {
        try {
            this.serverRemote = (InterfaceServerRMI) LocateRegistry.getRegistry(this.hostname, this.port).lookup("SayMore");
            if (this.login) {
                this.serverRemote.loginRequest(user, rmiReceiver, false);
            }
            iniciate();
            return true;
        } catch (Exception e) {
            return false;
        }
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

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: java ClientRMI hostname.");
        } else {
            new ClientRMI(args[0]);
        }

    }
}
