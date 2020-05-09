package application;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.internal.ws.org.objectweb.asm.Label;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ClientController {

	public boolean changed = false;
	public String fromServ;
	public Client myClient;
	public ArrayList<Item> items;
	public ArrayList<String> names;
	public int boxIndex;
    private PrintWriter toServer;
    private static String host = "127.0.0.1";
    private BufferedReader fromServer;
    private static String customerID;
    private String password;
    public loginController login;
    public Scene loginScene;
    
    @FXML
    private Button bidButton;
    @FXML
    private TextField bidText;
    @FXML
    private ChoiceBox<String> itemsBox;
    @FXML
    private Label customerName;
    @FXML
    private TextArea descriptionText;
    @FXML
    private TextField timeText;
    @FXML
    private TextField lowestBidText;
    @FXML
    private Button logoutButton;
    @FXML
    private Button quitButton;
    @FXML
    private TextField ownerText;
    
    public ClientController() {
    	items = new ArrayList<Item>();
    	itemsBox = new ChoiceBox<String>();
    	login = new loginController();
    	names = new ArrayList<String>();
    	customerID = "";
    	password = "";
    	boxIndex = -1;
    	AnimationTimer timer = new myTimer();
    	timer.start();
    	try {
			setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @FXML
    public void initialize() {
    	// add a listener 
        itemsBox.getSelectionModel().selectedIndexProperty().addListener(
        		new ChangeListener<Number>() { 
  
            // if the item of the list is changed 
            public void changed(ObservableValue ov, Number value, Number new_value) 
            { 
            	boxIndex = new_value.intValue();
            	Item temp = items.get(boxIndex);
            	descriptionText.setText(temp.description);
            	timeText.setText(Long.toString(temp.timeRemaining));
            	lowestBidText.setText(Double.toString(temp.minPrice));
            	if(temp.owner.username.equals("")) {
            		ownerText.setText("Be the first to bid!");
            	}
            	else ownerText.setText(temp.owner.username);
            } 
        }); 
    }
    
    public void bidButtonPressed() {
    	bidText.setPromptText("How much?");
		try {
			double bid = Double.parseDouble(bidText.getText());
			if(bid>items.get(boxIndex).minPrice) {
				items.get(boxIndex).minPrice = bid;
				items.get(boxIndex).owner = new Customer(customerID, password);
				lowestBidText.setText(bidText.getText());
				ownerText.setText(customerID);
				GsonBuilder builder = new GsonBuilder();
				Gson gson = builder.create();
				Message info = new Message("bid",gson.toJson(items.get(boxIndex)),boxIndex);
				changed = true;
				sendToServer(gson.toJson(info));
			} else {
				bidText.clear();
				bidText.setPromptText("too low!");
			}
		}
		catch(Exception e) {
			bidText.clear();
			bidText.setPromptText("not a number!");
		}
    }
    
    public void logoutButtonPressed() {
    	login.primaryStage.setTitle("Login");
    	login.primaryStage.setScene(loginScene);
    }
    
    public void quitButtonPressed() {
    	System.exit(0);
    }
    
    public void setID(String id, String password) {
    	customerID = id;
    	this.password = password;
    	
    }
    
    public boolean initialized = false;
    
    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")
        Socket socket = new Socket(host, 4242);
        System.out.println("Connecting to... " + socket);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toServer = new PrintWriter(socket.getOutputStream());

        Thread readerThread = new Thread(new Runnable() {
        	@Override
        	public void run() {
        		String input;
        		try {
        			while ((input = fromServer.readLine()) != null) {
        				Gson gson = new Gson();
        				Message message = gson.fromJson(input, Message.class);
        					String temp = "";
        					switch (message.type) {
        					case "upper":
        						temp = message.input.toUpperCase();
        						break;
        					case "lower":
        						temp = message.input.toLowerCase();
        						break;
        					case "strip":
        						temp = message.input.replace(" ", "");
        						break;
        					case "item":
        						if(items.size()>0) {
        						Item tempy = gson.fromJson(message.input, Item.class);
        						items.get(message.number).minPrice = tempy.minPrice;
        						items.get(message.number).owner = tempy.owner;
        						items.get(message.number).timeRemaining = tempy.timeRemaining;
        						if(message.number == boxIndex) {
        							Platform.runLater(()->{
        								lowestBidText.setText(Double.toString(tempy.minPrice));
        								ownerText.setText(tempy.owner.username);
        							});
        							//timeText.setText(Long.toString(tempy.timeRemaining));
        						}
        						}
        						break;
        					case "itemArray":
        						if(initialized == false) {
        							initialized = true;
        							Type ItemListType = new TypeToken<ArrayList<Item>>(){}.getType(); 
        							items = gson.fromJson(message.input, ItemListType);
        							names.clear();
        							for(int i=0; i<items.size(); i++) {
        								names.add(items.get(i).name);
        							}
        							Platform.runLater(()->{
        								itemsBox.setItems(FXCollections.observableArrayList(names));
        							});	
        						}
        						break;
        					case "validUser":
        						Customer valid = gson.fromJson(message.input, Customer.class);
        						if(customerID.equals(valid.username) && password.equals(valid.password)) {
        							Platform.runLater(()->{
        								loginScene = login.primaryStage.getScene();
        								login.primaryStage.setTitle("Welcome, " + customerID);
        								login.primaryStage.setScene(login.primaryScene);
        							});
        						}
        						break;
        					case "invalidUser":
        						//Customer invalid = gson.fromJson(message.input, Customer.class);
        						customerID = "";
        						password = "";
        						Platform.runLater(()->{
        							login.loginInvalid();
        						});
        					}
        					System.out.println("From server: " + input);
        					processRequest(input);
        				}
        			} catch (Exception e) {
        				e.printStackTrace();
        		}
        	}
        });
        readerThread.start();
      }
    
    public void sendToServer(Customer customer) {
    	customerID = customer.username;
    	password = customer.password;
    	GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Message message = new Message("user",gson.toJson(customer),1);
		sendToServer(gson.toJson(message));
    }
    
    protected void processRequest(String input) {
        return;
      }
    protected void sendToServer(String string) {
        System.out.println("Sending to server: " + string);
        toServer.println(string);
        toServer.flush();
      }
    
    private class myTimer extends AnimationTimer {
		
		@Override
		public void handle(long now) {
			if(boxIndex >= 0) {
				timeText.setText(Long.toString(items.get(boxIndex).timeRemaining));
				//lowestBidText.setText(Double.toString(items.get(boxIndex).minPrice));
				//ownerText.setText(items.get(boxIndex).owner.username);
			}
		}
    }
}
