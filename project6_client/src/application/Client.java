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

  @Override
  public void start(Stage primaryStage) throws Exception {
	  FXMLLoader fxmlLoader = new FXMLLoader();        
	  Parent root = fxmlLoader.load(getClass().getResource("client.fxml").openStream());        
	  controller = fxmlLoader.getController();        
	  primaryStage.setTitle("Login");
	 
	  FXMLLoader loader = new FXMLLoader();
	  Stage loginStage = new Stage();
	  Parent loginRoot = loader.load(getClass().getResource("login.fxml").openStream());
	  ((loginController) loader.getController()).setStage(primaryStage, new Scene(root,620,600), controller);
	  Scene loginScene = new Scene(loginRoot,500,200);
	  primaryStage.setScene(loginScene);
	  primaryStage.show();
  }

}