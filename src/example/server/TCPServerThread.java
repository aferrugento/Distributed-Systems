package example.server;


import java.net.*;
import java.io.*;
import java.util.Calendar;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class TCPServerThread extends Thread {

    private TCPServer server = null;
    private Socket socket = null;
    private int ID = -1;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    private User user;
    private boolean login;

    public TCPServerThread(TCPServer server, Socket socket) {
        super();
        this.server = server;
        this.socket = socket;
        this.ID = socket.getPort();
        this.user = null;
        this.login = false;
    }

    public void send(String msg) {
        try {
            streamOut.writeUTF(msg);
            streamOut.flush();
        } catch (IOException ioe) {
            System.out.println(ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
            stop();
        }
    }

    public int getID() {
        return ID;
    }

    public void logout() {
        this.send("Logging out!!");
        server.remove(this.ID);
        server.handle(this.user, "just disconnected.", true);

    }

    public void menu1() {
        this.send("\n				.:SAYMORE:.				\n\n\nWelcome to SAYMORE! Choose your option:\n\n  (1) Sign up\n  (2) Sign in\n\n:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.");
        String choice = null;
        try {
            choice = streamIn.readUTF();
        } catch (IOException ioe) {
            System.out.println("Error reading from socket.");
        }

        if (choice.equals("1")) {
            register();
            return;
        } else if (choice.equals("2")) {
            loginRequest();
            return;
        } else {
            this.send("Invalid choice. Choose 1 or 2.");
            menu1();
            return;

        }

    }

    public void menu2() {
        this.send("\n  (1)  Make a post\n  (2)  Make a delayed post\n  (3)  Edit a post\n  (4)  Delete a post\n  (5)  Reply to post\n  (6)  Edit reply to post\n  (7)  Delete reply to post\n  (8)  Make a post wich is a picture\n  (9)  Send direct message\n  (10) See messages\n  (11) See posts\n  (12) See replies to a post \n  (13) Show online users\n  (14) Sign out\n\n:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.:.");
        String choice = null;
        int choiceNumber = 0;

        try {
            choice = streamIn.readUTF();
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            menu2();
        }
        try {
            choiceNumber = Integer.parseInt(choice, 10);
        } catch (NumberFormatException nfe) {
            this.send("Invalid choice. Choose between 1 and 14.");
            menu2();
        }

        switch (choiceNumber) {
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
                image();
                menu2();
                break;
            case 9:
                message();
                menu2();
                break;
            case 10:
                listMessages();
                menu2();
                break;
            case 11:
                listPosts();
                menu2();
                break;
            case 12:
                listComments();
                menu2();
                break;
            case 13:
                listOnline();
                menu2();
                break;
            case 14:
                logout();
                break;

            default:
                this.send("Invalid choice. Choose between 1 and 14.");
                menu2();
                break;

        }
    }

    public void newMessages() {

        int count = 0;
        for (int i = 0; i < server.messages.size(); i++) {
            if (server.messages.get(i).getReceiver().getName().equals(this.user.getName()) && (!(server.messages.get(i).getSeen()))) {
                count++;
            }
        }
        this.send("You have " + count + " new messages!");
    }

    public User searchUser(String name) {
        int i;
        for (i = 0; i < server.users.size(); i++) {
            if (server.users.get(i).getName().equals(name)) {
                return server.users.get(i);
            }
        }

        return null;
    }

    public void loginRequest() {
        User temp = null;
        String tempName = null;
        this.send("Enter your username: ");
        try {
            tempName = streamIn.readUTF();
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            loginRequest();
        }


        System.out.println(searchUser(tempName));
        if ((temp = searchUser(tempName)) == null) {
            this.send("That username doesn't exist. Try again[1] or get back to menu[2]? ");
            try {
                if (streamIn.readUTF().equals("1")) {
                    loginRequest();
                    return;
                } else {
                    menu1();
                    return;
                }
            } catch (IOException ioe) {
                this.send("Error reading from socket.");
                loginRequest();
            }

        } else {
            this.send("Enter your password: ");
            try {

                if (!temp.authentication(streamIn.readUTF())) {
                    this.send("Wrong password or username.");
                    loginRequest();
                    return;
                }

            } catch (IOException ioe) {
                this.send("Error reading from socket.");
                loginRequest();
            }

            this.user = temp;
            this.login = true;
            this.send("Login done!");
            this.send("Logging in!!");
            try {
                for (int i = 0; i < server.users.size(); i++) {
                    if (server.users.get(i).getName().equals(this.user.getName())) {
                        tempName = i + "";
                        streamOut.writeUTF(tempName);
                        break;
                    }
                }
            } catch (IOException e) {
                this.send("Error writing on socket.");
                loginRequest();
            }

            server.handle(this.user, "is now connected!", true);
        }
    }

    public void register() {
        String tempName = null;
        String tempPass = null;

        this.send("Enter a username: ");
        try {

            tempName = streamIn.readUTF();

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            register();
        }

        System.out.println(searchUser(tempName));
        if ((searchUser(tempName)) != null) {
            this.send("That username has already being used. Try again[1] or get back to menu[2]? ");
            try {
                if (streamIn.readUTF().equals("1")) {
                    register();
                    return;
                } else {
                    menu1();
                    return;
                }
            } catch (IOException ioe) {
                this.send("Error reading from socket.");
                register();
            }

        } else {

            this.send("Enter a password: ");
            try {
                tempPass = streamIn.readUTF();
            } catch (IOException ioe) {
                this.send("Error reading from socket.");
                register();
            }

            server.users.add(this.user = new User(tempName, tempPass));

            this.send("Registration and login done!");
            this.send("Logging in!!");
            login = true;
            try {
                for (int i = 0; i < server.users.size(); i++) {
                    if (server.users.get(i).getName().equals(this.user.getName())) {
                        tempName = i + "";
                        streamOut.writeUTF(tempName);
                        break;
                    }
                }
            } catch (IOException e) {
                this.send("Error writing from socket.");
                register();
            }

            server.handle(this.user, "is now connected!", true);
        }

    }

    public void listMessages() {

        for (int i = 0; i < server.messages.size(); i++) {
            if (server.messages.get(i).getReceiver().getName().equals(this.user.getName())) {
                this.send("By: " + server.messages.get(i).getAuthor().getName() + " " + server.messages.get(i).format());
            }
        }
    }

    public void listPosts() {
        int i = 0, outro_counter = 0;
        while(outro_counter < server.posts.size())
        {
            if(server.posts.containsKey(i))
            {
                outro_counter++;
                this.send("[ID=" + i + "] \n" + server.posts.get(i).getAuthor().getName() + ": " + server.posts.get(i).getPost());
            }
            i++;
        }
    }

    public void listOnline() {
        for (int i = 0; i < server.clients.size(); i++) {
            this.send(server.clients.get(i).getUser().getName());
        }

    }

    public void listComments() {
        Post chosenPost = null;
        String postID = null;
        int aux = 0;
        this.send("Enter the post ID: ");

        try {
            postID = streamIn.readUTF();
            aux = Integer.parseInt(postID, 10);
            if (!server.posts.containsKey(aux)) {
                this.send("Invalid ID. Try again[1] or get back to menu[2]?");
                if (streamIn.readUTF().equals("1")) {
                    listComments();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            listComments();
        } catch (NumberFormatException nfe) {
            this.send("Invalid ID.");
            listComments();
        }

        chosenPost = server.posts.get(aux);

        this.send("Is this post: " + chosenPost.getPost() + "? [y/n]");

        try {
            if (!streamIn.readUTF().equalsIgnoreCase("y")) {
                this.send("Try again[1] or get back to menu[2]? ");
                if (streamIn.readUTF().equals("1")) {
                    listComments();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            listComments();
        }

        for (int i = 0; i < server.posts.get(Integer.parseInt(postID)).getList_comments().size(); i++) {
            this.send("[ID=" + i + "] " + server.posts.get(Integer.parseInt(postID)).getList_comments().get(i).getAuthor().getName() + ": " + server.posts.get(Integer.parseInt(postID)).getList_comments().get(i).getComment());
        }

    }

    public void delayPost() {
        String day = null;
        String month = null;
        String hours = null;
        String minutes = null;
        String input = null;
        Calendar date;

        this.send("Type the post:");
        try {
            input = streamIn.readUTF();
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            delayPost();
        }
        this.send("Enter the time to publish the post.\n month: ");
        try {
            month = streamIn.readUTF();
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            delayPost();
        }
        this.send(" day: ");
        try {
            day = streamIn.readUTF();
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            delayPost();
        }

        this.send(" hours: ");
        try {
            hours = streamIn.readUTF();
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            delayPost();
        }

        this.send(" minutes: ");
        try {
            minutes = streamIn.readUTF();
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
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
            this.send("Invalid date.");
            delayPost();
        }

        if (date.before(current)) {
            try {
                this.send("Invalid date. Try again[1] or get back to menu[2]?");
                if (streamIn.readUTF().equals("1")) {
                    delayPost();
                    return;
                } else {
                    menu2();
                    return;
                }
            } catch (IOException ioe) {
                this.send("Error reading from socket.");
                delayPost();
            }
        }

        server.delayedPosts.awake();
        server.delayedPosts.addNew(new DelayedPost(date, this.user, input, null));
        server.handle(this.user, "made a new delayed post.", true);

    }

    public void deleteReply() {
        Post chosenPost = null;
        Reply chosenReply = null;
        String postID = null;
        String replyID = null;
        int aux = 0;
        this.send("Enter the post ID: ");

        try {
            postID = streamIn.readUTF();
            aux = Integer.parseInt(postID, 10);
            if (!server.posts.containsKey(aux)) {
                this.send("Invalid ID. Try again[1] or get back to menu[2]?");
                if (streamIn.readUTF().equals("1")) {
                    deleteReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            deleteReply();
        } catch (NumberFormatException nfe) {
            this.send("Invalid ID.");
            deleteReply();
        }
        chosenPost = server.posts.get(aux);
        this.send("Is this post: " + chosenPost.getPost() + "? [y/n]");

        try {
            if (!streamIn.readUTF().equalsIgnoreCase("y")) {
                this.send("Try again[1] or get back to menu[2]? ");
                if (streamIn.readUTF().equals("1")) {
                    deleteReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            deleteReply();
        }


        this.send("Enter the reply ID: ");

        try {
            replyID = streamIn.readUTF();
            if ((aux = Integer.parseInt(replyID, 10)) >= chosenPost.getList_comments().size() || aux < 0 || !((chosenReply = chosenPost.getList_comments().get(aux)).getAuthor().getName().equals(this.user.getName()))) //VERIFICAR SE O COMENTARIO E DELE
            {
                this.send("Invalid ID. Try again[1] or get back to menu[2]?");
                if (streamIn.readUTF().equals("1")) {
                    deleteReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            deleteReply();
        } catch (NumberFormatException nfe) {
            this.send("Invalid ID.");
            deleteReply();
        }

        this.send("Is this reply: " + chosenReply.getComment() + "? [y/n]");
        try {
            if (!streamIn.readUTF().equalsIgnoreCase("y")) {
                this.send("Try again[1] or get back to menu[2]? ");
                if (streamIn.readUTF().equals("1")) {
                    deleteReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            deleteReply();
        }

        chosenPost.getList_comments().remove(aux);

    }

    public void editReply() {
        Post chosenPost = null;
        Reply chosenReply = null;
        String postID = null;
        String replyID = null;
        int aux = 0;
        this.send("Enter the post ID: ");

        try {
            postID = streamIn.readUTF();
            aux = Integer.parseInt(postID, 10);
            if (!server.posts.containsKey(aux)) {
                this.send("Invalid ID. Try again[1] or get back to menu[2]?");
                if (streamIn.readUTF().equals("1")) {
                    editReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            editReply();
        } catch (NumberFormatException nfe) {
            this.send("Invalid ID.");
            editReply();
        }

        chosenPost = server.posts.get(aux);

        this.send("Is this post: " + chosenPost.getPost() + "? [y/n]");

        try {
            if (!streamIn.readUTF().equalsIgnoreCase("y")) {
                this.send("Try again[1] or get back to menu[2]? ");
                if (streamIn.readUTF().equals("1")) {
                    editReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            editReply();
        }

        this.send("Enter the reply ID: ");

        try {
            replyID = streamIn.readUTF();
            if ((aux = Integer.parseInt(replyID, 10)) >= chosenPost.getList_comments().size() || aux < 0 || (!((chosenReply = chosenPost.getList_comments().get(aux)).getAuthor().getName().equals(this.user.getName())))) {
                this.send("Invalid ID. Try again[1] or get back to menu[2]?");
                if (streamIn.readUTF().equals("1")) {
                    editReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            editReply();
        } catch (NumberFormatException nfe) {
            this.send("Invalid ID.");
            editReply();
        }

        this.send("Is this reply: " + chosenReply.getComment() + "? [y/n]");
        try {
            if (!streamIn.readUTF().equalsIgnoreCase("y")) {
                this.send("Try again[1] or get back to menu[2]? ");
                if (streamIn.readUTF().equals("1")) {
                    editReply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            editReply();
        }

        this.send("Type the new reply: ");
        try {

            chosenPost.getList_comments().set(aux, new Reply(streamIn.readUTF(), this.user));
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            editReply();
        }
        server.handle(this.user, "edited a reply in post number " + postID, true);
    }

    public void image() {
        String o = null;
        this.send("Enter the image path: ");
        try {
            o = streamIn.readUTF();
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            image();
        }
        server.handle(this.user, o, false);
        server.handle(this.user, "added a new picture.", true);

    }

    public void deletePost() {
        Post chosenPost = null;
        String postID = null;
        int aux = 0;
        this.send("Enter the post ID: ");
        try {
            postID = streamIn.readUTF();
            aux = Integer.parseInt(postID, 10);
            if ((!server.posts.containsKey(aux)) || (!(chosenPost = server.posts.get(aux)).getAuthor().getName().equals(this.user.getName()))) {
                this.send("Invalid ID. Try again[1] or get back to menu[2]?");
                if (streamIn.readUTF().equals("1")) {
                    deletePost();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            deletePost();
        } catch (NumberFormatException nfe) {
            this.send("Invalid ID.");
            deletePost();
        }


        this.send("Is this post: " + chosenPost.getPost() + "? [y/n]");

        try {
            if (!streamIn.readUTF().equalsIgnoreCase("y")) {
                this.send("Try again[1] or get back to menu[2]? ");
                if (streamIn.readUTF().equals("1")) {
                    deletePost();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            deletePost();
        }
            server.posts.remove(aux, chosenPost);
    }

    public void message() {

        String mess = null;
        String title = null;
        String subject = null;
        String dest = null;
        User chosenUsr = null;

        this.send("Enter the username of destination: ");
        try {
            dest = streamIn.readUTF();

            if ((chosenUsr = searchUser(dest)) == null) {
                this.send("Invalid username. Try again[1] or get back to menu[2]?");
                if (streamIn.readUTF().equals("1")) {
                    message();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            message();
        }

        this.send("Type the title: ");
        try {
            title = streamIn.readUTF();
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            message();
        }

        this.send("Type the subject: ");
        try {
            subject = streamIn.readUTF();

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            message();
        }

        try {
            this.send("Type the message: ");
            mess = streamIn.readUTF();
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            message();
        }

        server.handleMessages(new Message(title, subject, mess, this.user, chosenUsr, false));
    }

    public void post() {

        this.send("Type the post:");
        try {
            server.handle(this.user, streamIn.readUTF(), false);
        } catch (IOException ioe) {
            System.out.println(" Error reading from socket.");
            server.remove(ID);
            stop();
        }
    }

    public void editPost() {
        Post chosenPost = null;
        String postID = null;
        String newPost = null;
        int aux = 0;
        this.send("Enter the post ID: ");
        try {
            postID = streamIn.readUTF();
            aux = Integer.parseInt(postID, 10);
            if ((!server.posts.containsKey(aux)) || (!(chosenPost = server.posts.get(aux)).getAuthor().getName().equals(this.user.getName()))) {
                this.send("Invalid ID. Try again[1] or get back to menu[2]?");
                if (streamIn.readUTF().equals("1")) {
                    editPost();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            editPost();
        } catch (NumberFormatException nfe) {
            this.send("Invalid ID.");
            editPost();
        }

        this.send("Is this post: " + chosenPost.getPost() + "? [y/n]");
        try {
            if (!streamIn.readUTF().equalsIgnoreCase("y")) {
                this.send("Try again[1] or get back to menu[2]? ");
                if (streamIn.readUTF().equals("1")) {
                    editPost();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            editPost();
        }

        this.send("Type the new post: ");
        try {

            newPost = streamIn.readUTF();

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            editPost();
        }

        chosenPost.setPost(newPost);
        server.handle(this.user, "edited the post number " + postID, true);
    }

    public void reply() {
        Post chosenPost = null;
        String postID = null;

        int aux = 0;
        this.send("Enter the post ID: ");
        try {
            postID = streamIn.readUTF();
            aux = Integer.parseInt(postID, 10);
            if (!server.posts.containsKey(aux)) {
                this.send("Invalid ID. Try again[1] or get back to menu[2]?");
                if (streamIn.readUTF().equals("1")) {
                    reply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }

        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            reply();
        } catch (NumberFormatException nfe) {
            this.send("Invalid ID.");
            reply();
        }

        try {
            chosenPost = server.posts.get(Integer.parseInt(postID, 10));
            this.send("Is this post: " + chosenPost.getPost() + "? [y/n]");

            if (!streamIn.readUTF().equalsIgnoreCase("y")) {
                this.send("Try again[1] or get back to menu[2]? ");
                if (streamIn.readUTF().equals("1")) {
                    reply();
                    return;
                } else {
                    menu2();
                    return;
                }
            }
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            reply();
        } catch (NumberFormatException nfe) {
            this.send("Invalid ID.");
            reply();
        }


        try {
            this.send("Type the reply: ");

            chosenPost.getList_comments().add(new Reply(streamIn.readUTF(), this.user));
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
            reply();
        }
        server.handle(this.user, "made a reply in post number " + postID, true);
    }

    public void run() {
        int userInt;
        String temp;
        System.out.println("Server Thread " + ID + " running.");
        try {
            temp = streamIn.readUTF();
            if (temp.equals("Not logged in!!")) {
                menu1();
                newMessages();
                if (this.login) {
                    menu2();
                }
            } else {
                userInt = Integer.parseInt(temp);
                this.user = server.users.get(userInt);

                menu2();
            }
        } catch (IOException ioe) {
            this.send("Error reading from socket.");
        }
    }

    public void open() throws IOException {
        streamIn = new DataInputStream(socket.getInputStream());
        streamOut = new DataOutputStream(socket.getOutputStream());

    }

    public void close() throws IOException {

        if (socket != null) {
            socket.close();
        }

        if (streamIn != null) {
            streamIn.close();
        }

        if (streamOut != null) {
            streamOut.close();
        }
    }

    public boolean loginStat() {
        return this.login;
    }

    public User getUser() {
        return this.user;
    }
}