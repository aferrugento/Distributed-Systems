package example.server;


import java.util.*;
import java.io.Serializable;

public class DelayedPost extends Post implements Serializable {

    private Calendar time;

    public DelayedPost(Calendar time, User author, String post, String facebook) {
        super(author, post, facebook);
        this.time = time;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }
}
