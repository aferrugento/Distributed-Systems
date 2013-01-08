package example.server;


import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String pass;

    public User(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return this.pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean authentication(String password) {
        if (password.equals(this.pass)) {
            return true;
        }

        return false;
    }
}