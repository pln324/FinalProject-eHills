package application;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application{

  private static String host = "127.0.0.1";
  private BufferedReader fromServer;
  public PrintWriter toServer;
  private Scanner consoleInput = new Scanner(System.in);
  public ClientController controller;
  
  public static void main(String[] args) {
	  launch(args);
  }

//  private void setUpNetworking() throws Exception {
//    @SuppressWarnings("resource")
//    Socket socket = new Socket(host, 4242);
//    System.out.println("Connecting to... " + socket);
//    fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//    toServer = new PrintWriter(socket.getOutputStream());
//
//    Thread readerThread = new Thread(new Runnable() {
//      @Override
//      public void run() {
//        String input;
//        try {
//          while ((input = fromServer.readLine()) != null) {
//            System.out.println("From server: " + input);
//            processRequest(input);
//          }
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    });
//
//    Thread writerThread = new Thread(() ->{
//        while (true) {
//        		String input = consoleInput.nextLine();
//        		String[] variables = input.split(",");
//        		Message request = new Message(variables[0], variables[1], Integer.valueOf(variables[2]));
//        		GsonBuilder builder = new GsonBuilder();
//        		Gson gson = builder.create();
//        		sendToServer(gson.toJson(request));
//        	
//        }
//      });
//
//    readerThread.start();
//    writerThread.start();
//  }
//
//  protected void processRequest(String input) {
//    return;
//  }
//
//  protected void sendToServer(String string) {
//    System.out.println("Sending to server: " + string);
//    toServer.println(string);
//    toServer.flush();
//  }

  @Override
  public void start(Stage primaryStage) throws Exception {
	  FXMLLoader fxmlLoader = new FXMLLoader();        
	  Parent root = fxmlLoader.load(getClass().getResource("client.fxml").openStream());        
	  controller = fxmlLoader.getController();        
	  primaryStage.setTitle("Login");
	 
	  FXMLLoader loader = new FXMLLoader();
	  Stage loginStage = new Stage();
	  Parent loginRoot = loader.load(getClass().getResource("login.fxml").openStream());
	  ((loginController) loader.getController()).setStage(primaryStage, new Scene(root,700,600), controller);
	  Scene loginScene = new Scene(loginRoot);
	  
	  //primaryStage.setScene(new Scene(root, 700, 600));
	  primaryStage.setScene(loginScene);
	  primaryStage.show();
	  //controller.myClient = this;
	  
//	  FXMLLoader loader = new FXMLLoader();
//	  Stage loginStage = new Stage();
//	  Parent loginRoot = loader.load(getClass().getResource("login.fxml").openStream());
//	  Scene loginScene = new Scene(loginRoot);
//	  loginStage.setTitle("Log in to EHills");
//	  loginStage.setScene(loginScene);
//	  loginStage.show();
	  
//	  try {
//	      new Client().setUpNetworking();
//	    } catch (Exception e) {
//	      e.printStackTrace();
//	    }
  }

}