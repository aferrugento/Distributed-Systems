package example.server;


import java.io.Serializable;
import java.util.*;

public class Post implements Serializable {

    private String post;
    private User author;
    private Vector<Reply> list_comments;
    private String facebook;

    public Post(User author, String post, String facebook) {
        this.author = author;
        this.post = post;
        this.list_comments = new Vector<Reply>();
        this.facebook=facebook;
    }

    public String getFacebook()
    {
    	
    	return this.facebook;
    }
    
    
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Vector<Reply> getList_comments() {
        return this.list_comments;
    }

    public void setList_comments(Vector<Reply> list_comments) {
        this.list_comments = list_comments;
    }

    public String getPost() {
        return this.post;
    }

    public void setPost(String post) {
        this.post = post;
    }
}