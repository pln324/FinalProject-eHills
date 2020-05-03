import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

import javafx.application.Platform;

/*
 * Author: Vallath Nandakumar and the EE 422C instructors.
 * Data: April 20, 2020
 * This starter code assumes that you are using an Observer Design Pattern and the appropriate Java library
 * classes.  Also using Message objects instead of Strings for socket communication.
 * See the starter code for the Chat Program on Canvas.  
 * This code does not compile.
 */
public class Server extends Observable {

    static Server server;

    public static void main (String [] args) {
        server = new Server();
        //server.populateItems();
        server.SetupNetworking();
    }

    private void SetupNetworking() {
        int port = 5000;
        try {
            ServerSocket ss = new ServerSocket(port);
            while (true) {
                Socket clientSocket = ss.accept();
                ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
                Thread t = new Thread(new ClientHandler(clientSocket, writer));
                t.start();
                addObserver(writer);
                System.out.println("got a connection");
            }
        } catch (IOException e) {}
    }

    class ClientHandler implements Runnable {
        private  ObjectInputStream reader;
        private  ClientObserver writer; // See Canvas. Extends ObjectOutputStream, implements Observer
        Socket clientSocket;

        public ClientHandler(Socket clientSocket, ClientObserver writer) {
			this.writer = writer;
			this.clientSocket = clientSocket;
        }

        public void run() {
        	try {
        		// Create data input and output streams
        		reader = new ObjectInputStream( clientSocket.getInputStream());
        		ObjectOutputStream outputToClient = new ObjectOutputStream( clientSocket.getOutputStream());
        		// Continuously serve the client
        		while (true) { 
        			// Receive radius from the client 
        			double radius = reader.readDouble();
        			// Compute area
        			double area = radius * radius * Math.PI; 
        			// Send area back to the client
        			outputToClient.writeDouble(area);
        			Platform.runLater(() -> { 
        			
        			});
        		}
        	} catch(IOException e) {
        		e.printStackTrace();
        	}
        }
    }// end of class ClientHandler
}
