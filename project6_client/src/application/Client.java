package application;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;

/*
 * Author: Vallath Nandakumar and EE 422C instructors
 * Date: April 20, 2020
 * This starter code is from the MultiThreadChat example from the lecture, and is on Canvas. 
 * It doesn't compile.
 */

public class Client extends Application {

    ClientController controller;
    ObjectInputStream reader;
    ObjectOutputStream writer;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("client.fxml").openStream());
        controller = fxmlLoader.getController();
        primaryStage.setTitle("Customer");
        primaryStage.setScene(new Scene(root, 700, 600));
        primaryStage.show();
        controller.myClient = this;
        customer = new Customer("", "");

        connectToServer();
    }

    void connectToServer () {
        int port = 5000;
        try {
            Socket sock = new Socket("localhost", port);
            writer = new ObjectOutputStream(sock.getOutputStream());
            reader = new ObjectInputStream(sock.getInputStream());
            System.out.println("networking established");
            Thread readerThread = new Thread(new IncomingReader()); // see Canvas's Chat for IncomingReader class
            readerThread.start();

        } catch (IOException e) {}
    }

    ClientController getController () { return controller; }
	
	public static void main(String[] args) {
		launch(args);
	}
}
