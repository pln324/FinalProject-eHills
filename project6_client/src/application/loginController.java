package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class loginController {
	
	@FXML
	private TextField userText;
	@FXML
	private PasswordField passText;
    @FXML
    private Button loginButton;
    @FXML
    private DialogPane dp;
	public Stage primaryStage;
	public Scene primaryScene;
	public ClientController controller;
	public Parent root;
	volatile static int currentID;
	
	public void setStage(Stage stage, Scene scene, ClientController controller){
		primaryStage=stage;
		primaryScene=scene;
		this.controller = controller;
	}
	
	public void loginButtonPressed () {
		String user = userText.getText();
		if(user.equals("")) {
			user = "guest" + currentID;
			currentID++;
		} 
		controller.setID(user, passText.getText());
		primaryStage.setTitle("Welcome, "+ user + "!");
		primaryStage.setScene(primaryScene);
	}
}
