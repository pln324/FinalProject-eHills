package final_exam;

import java.io.PrintWriter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application{

  public PrintWriter toServer;
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
	  Parent loginRoot = loader.load(getClass().getResource("login.fxml").openStream());
	  Scene loginScene = new Scene(loginRoot,500,200);
	  ((loginController) loader.getController()).setStage(primaryStage, new Scene(root,620,600), loginScene, controller);
	  primaryStage.setScene(loginScene);
	  primaryStage.show();
  }

}