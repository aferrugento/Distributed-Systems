package example.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.imageio.ImageIO;


import example.teste.MessageWebSocketServlet;
import example.teste.MessageWebSocketServlet.MMessageInbound;
import example.teste.ServerManager;


public class RMIReceiver extends UnicastRemoteObject implements
		InterfaceClientRMI {


	public RMIReceiver() throws RemoteException {
		super();
		
	}

	public void receive(String msg, boolean web, User user) {

		if (web) {
			if (msg.endsWith("is now connected!") || msg.endsWith("just disconnected.") || msg.startsWith("You have")) {
				for (MMessageInbound connection : MessageWebSocketServlet.connections) {
					if (user.getName().equals(connection.getUser().getName())) {
						connection.send(msg);
					}
				}

			}
		} else {

			System.out.println(msg);
		}
	}
	
	
	public void receiveImage(byte[] rawImage, boolean web, User user, String name) {

		BufferedImage img=null;
        try {
			 img = javax.imageio.ImageIO.read(new ByteArrayInputStream(rawImage));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
		if (web) {

			ServerManager.addImage();
			
		}
		
		 else {
			 ASCIIConverter conv = new ASCIIConverter();
             String ascii = conv.convert(img);
             receive(ascii, web, user);
			
		}
	}
	
}
