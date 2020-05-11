package final_exam;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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
	public Scene loginScene;
	public ClientController controller;
	public Parent root;
	volatile static int currentID;
	
	public void setStage(Stage stage, Scene scene, Scene loginScene, ClientController controller){
		primaryStage=stage;
		primaryScene=scene;
		this.loginScene = loginScene;
		this.controller = controller;
		controller.login = this;
	}
	
	public void quitButtonPressed() {
		String musicFile = "buttonSound.mp3";
    	Media sound = new Media(new File(musicFile).toURI().toString());
    	MediaPlayer mediaPlayer = new MediaPlayer(sound);
    	mediaPlayer.play();
		System.exit(0);
    }
	
	public void loginButtonPressed () {
		String user = userText.getText();
		String musicFile = "buttonSound.mp3";
    	Media sound = new Media(new File(musicFile).toURI().toString());
    	MediaPlayer mediaPlayer = new MediaPlayer(sound);
		if(user.contains(" ") || user.contentEquals("")) {
			loginInvalid("not a valid username");
			return;
		}
		mediaPlayer.play();
		Customer customer = new Customer(user,passText.getText());
		userText.clear();
		passText.clear();
		controller.sendToServer(customer);
	}
	
	public void loginInvalid(String error) {
		passText.clear();
		String musicFile = "errorSound.mp3";
    	Media sound = new Media(new File(musicFile).toURI().toString());
    	MediaPlayer mediaPlayer = new MediaPlayer(sound);
    	mediaPlayer.play();
		Alert a = new Alert(AlertType.NONE,error);
        a.setAlertType(AlertType.WARNING); 
        a.show(); 
	}
	
	public void newAccountButtonPressed() {
	
	}
	
	public void guestButtonPressed() {
	
	}
}
