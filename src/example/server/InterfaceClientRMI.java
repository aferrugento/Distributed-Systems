package example.server;


import java.awt.image.BufferedImage;
import java.rmi.*;

public interface InterfaceClientRMI extends Remote {

    public void receive(String msg, boolean web, User user) throws RemoteException;
    
    public void receiveImage(byte[] img, boolean web, User user, String name) throws RemoteException;
}