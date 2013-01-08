package example.server;


import java.io.Serializable;

public class Message implements Serializable {

    private String subject;
    private String title;
    private String content;
    private User author;
    private User receiver;
    private Boolean seen;

    public Message(String title, String subject, String content, User author, User receiver, Boolean seen) {
        this.subject = subject;
        this.title = title;
        this.content = content;
        this.author = author;
        this.receiver = receiver;
        this.seen = seen;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return this.author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getReceiver() {
        return this.receiver;
    }

    public void setReciever(User receiver) {
        this.receiver = receiver;
    }

    public String format() {
        return "Title: " + this.title + "\n" + "Subject: " + this.subject + "\n" + "Message: \n" + this.content;
    }
    
    public String format2() {
        return "Title: " + this.title + "<br>" + "Subject: " + this.subject + "<br>" + "Message: " + this.content;
    }
}