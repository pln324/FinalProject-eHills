package final_exam;

import java.io.IOException;

import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
    @FXML
    private Button newAccount;
    
	public Stage primaryStage;
	public Scene primaryScene;
	public ClientController controller;
	public Parent root;
	volatile static int currentID;
	
	public void setStage(Stage stage, Scene scene, ClientController controller){
		primaryStage=stage;
		primaryScene=scene;
		this.controller = controller;
		controller.login = this;
	}
	
	public void quitButtonPressed() {
    	System.exit(0);
    }
	
	public void loginButtonPressed () {
		String user = userText.getText();
		if(user.equals("")) {
			user = "guest" + currentID;
			currentID++;
		}
		Customer customer = new Customer(user,passText.getText());
		controller.sendToServer(customer);
	}
	
	public void loginInvalid() {
		passText.clear();
		Alert a = new Alert(AlertType.NONE,"incorrect password");
        a.setAlertType(AlertType.WARNING); 
        a.show(); 
	}
	
	public void newAccountButtonPressed() {
		
	}
}
