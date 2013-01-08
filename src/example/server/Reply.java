package example.server;


import java.io.Serializable;
import java.util.*;

public class Reply implements Serializable{

    private String comment;
    private User author;

    public Reply(String comment, User author) {
        this.comment = comment;
        this.author = author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }
}
