package example.server;


import java.awt.image.BufferedImage;
import java.io.File;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;

public class ClientRMIConnection {

    private User user;
    private InterfaceClientRMI remote;
    public boolean web;

    public ClientRMIConnection(User user, InterfaceClientRMI remote, boolean web) {
        this.user = user;
        this.remote = remote;
        this.web=web;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void send(String msg, User user) {
        try {
            this.remote.receive(msg, this.web, user);
            return;
        } catch (RemoteException e) {
            System.out.println("Remote exception caught.");
            send(msg, user);
            return;
        }
    }
   

    
    public void sendImage(byte[] img, User user, String name) {
        try {
            this.remote.receiveImage(img, this.web, user, name);
            return;
        } catch (RemoteException e) {
            System.out.println("Remote exception caught on sendImage.");
           // sendImage(img, user);
            return;
        }
    }

    public boolean auth(InterfaceClientRMI remote) {
        return this.remote.equals(remote);
    }
}
