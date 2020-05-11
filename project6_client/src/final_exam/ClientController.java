package final_exam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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
    private Customer customer;
    public loginController login;
    public Scene loginScene;
    public ArrayList<Item> bought;
    public int clientID;
    
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
    @FXML
    private TableView<Item> buyTable;
    @FXML
    private TextArea bidHistoryText;
    @FXML
    private ImageView imageFrame;
    @FXML
    private ImageView logo;
    
    public ClientController() {
    	items = new ArrayList<Item>();
    	itemsBox = new ChoiceBox<String>();
    	login = new loginController();
    	names = new ArrayList<String>();
    	bought = new ArrayList<Item>();
    	customer = new Customer();
    	imageFrame = new ImageView();
    	logo = new ImageView();
    	clientID = 0;
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
    	File file = new File("ehills.jpeg");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        
    	//initialize table of purchased items
    	TableColumn itemName = new TableColumn("Item");
        TableColumn description = new TableColumn("Description");
        TableColumn purchasePrice = new TableColumn("Price");
        itemName.setCellValueFactory(new PropertyValueFactory<Item,String>("name"));
        itemName.setMinWidth(100);
        description.setCellValueFactory(new PropertyValueFactory<Item,String>("description"));
        description.setMinWidth(425);
        purchasePrice.setCellValueFactory(new PropertyValueFactory<Item,Double>("minPrice"));
        purchasePrice.setMinWidth(75);
        buyTable.getColumns().clear();
        buyTable.getColumns().addAll(itemName,description,purchasePrice);
    	
    	// add a listener 
        itemsBox.getSelectionModel().selectedIndexProperty().addListener(
        		new ChangeListener<Number>() { 
  
            // if the item of the list is changed 
            public void changed(ObservableValue ov, Number value, Number new_value) 
            { 
            	try {
            		boxIndex = new_value.intValue();
            		Item temp = items.get(boxIndex);
            		descriptionText.setText(temp.description);
            		timeText.setText(Long.toString(temp.timeRemaining));
            		lowestBidText.setText(Double.toString(temp.minPrice));
            		bidHistoryText.setText(temp.bidHistory);
            		bidText.setEditable(true);
					bidButton.setDisable(false);
					bidText.setPromptText("how much would you like to bid");
					
					File file = new File(temp.image);
			        Image image = new Image(file.toURI().toString());
			        imageFrame.setImage(image);
					
            		if(temp.owner.username.equals("")) {
            			ownerText.clear();
            			ownerText.setPromptText("Be the first to bid!");
            		}
            		if(temp.sold == true) {
            			bidText.setEditable(false);
						bidButton.setDisable(true);
						bidText.clear();
						if(temp.owner.username.equals("")) bidText.setPromptText("Auction over with no bids :(");
						else bidText.setPromptText("Auction over! Winner: " + temp.owner.username);
						ownerText.setText(temp.owner.username);
            		}
            		else ownerText.setText(temp.owner.username);
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            } 
        }); 
    }
    
    public void bidButtonPressed() {
    	bidText.setPromptText("How much?");
		try {
			double bid = Double.parseDouble(bidText.getText());
			if(bid>items.get(boxIndex).minPrice && items.get(boxIndex).sold == false) {
				String musicFile = "bidSound.mp3";
		    	Media sound = new Media(new File(musicFile).toURI().toString());
		    	MediaPlayer mediaPlayer = new MediaPlayer(sound);
		    	mediaPlayer.play();
				items.get(boxIndex).minPrice = bid;
				items.get(boxIndex).owner = customer;
				items.get(boxIndex).bidHistory += customer.username + " bids $" + bidText.getText() + "\n";
				lowestBidText.setText(bidText.getText());
				ownerText.setText(customer.username);
				bidHistoryText.setText(items.get(boxIndex).bidHistory);
				bidText.setPromptText("how much would you like to bid");
				GsonBuilder builder = new GsonBuilder();
				Gson gson = builder.create();
				Message info = new Message("bid",gson.toJson(items.get(boxIndex)),boxIndex); //send bid to server to update
				sendToServer(gson.toJson(info));
			} else {
				String musicFile = "errorSound.mp3";
		    	Media sound = new Media(new File(musicFile).toURI().toString());
		    	MediaPlayer mediaPlayer = new MediaPlayer(sound);
		    	mediaPlayer.play();
				bidText.clear();
				bidText.setPromptText("too low!");
			}
		}
		catch(Exception e) {
			bidText.clear();
			bidText.setPromptText("not a number!");
		}
    }
    
    public void logoutButtonPressed() {			//return to login window
    	customer = new Customer();
    	String musicFile = "buttonSound.mp3";
    	Media sound = new Media(new File(musicFile).toURI().toString());
    	MediaPlayer mediaPlayer = new MediaPlayer(sound);
    	mediaPlayer.play();
    	login.primaryStage.setTitle("Login");
    	login.primaryStage.setScene(login.loginScene);
    }
    
    public void quitButtonPressed() {
    	String musicFile = "buttonSound.mp3";
    	Media sound = new Media(new File(musicFile).toURI().toString());
    	MediaPlayer mediaPlayer = new MediaPlayer(sound);
    	mediaPlayer.play();
    	System.exit(0);
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
        					switch (message.type) {
        					case "item":			//message contains item to update
        						if(items.size()>0) {
        							Item tempy = gson.fromJson(message.input, Item.class);
        							items.set(message.number, tempy);
        							if(message.number == boxIndex) {
        								Platform.runLater(()->{
        									lowestBidText.setText(Double.toString(tempy.minPrice));
        									ownerText.setText(tempy.owner.username);
        									bidHistoryText.setText(tempy.bidHistory);
        								});
        							}
        						}
        						break;
        					case "initialize":		//sends full list of items at start of client creation
        						if(initialized == false) {
        							initialized = true;
        							clientID = message.number;
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
        					case "validUser":		//username and password are correct or have not been created yet
        						Customer valid = gson.fromJson(message.input, Customer.class);
        						if(customer.username.equals(valid.username) && customer.password.equals(valid.password)) {
        							customer = valid;
        							Platform.runLater(()->{
        								loginScene = login.primaryStage.getScene();
        								login.primaryStage.setTitle("Welcome, " + customer.username + "!");
        								login.primaryStage.setScene(login.primaryScene);
        								buyTable.setItems(FXCollections.observableArrayList(customer.itemsOwned()));
        							});
        						}
        						break;
        					case "invalidUser":		//password incorrect, show alert window
        						if(message.number==clientID) {
        							Platform.runLater(()->{
        								login.loginInvalid("incorrect password");
        							});
        						}
        						break;
        					case "remove":			//once item auction is over, stop selling it
        						Item remove = gson.fromJson(message.input, Item.class);
        						items.set(message.number,remove);
        						if(remove.owner.username.equals(customer.username)) {
        							customer.itemPurchased(remove);
        							Message purchase = new Message("purchase",gson.toJson(items.get(message.number)),message.number);
        							sendToServer(gson.toJson(purchase));
        							Platform.runLater(()->{
        								String musicFile = "partyhorn.mp3";
        								Media sound = new Media(new File(musicFile).toURI().toString());
        						    	MediaPlayer mediaPlayer = new MediaPlayer(sound);
        						    	mediaPlayer.play();
        							});
        							Platform.runLater(()->{
        								Alert a = new Alert(AlertType.NONE,"Congrats, you have won the auction for " + remove.name + "!");
        						        a.setAlertType(AlertType.INFORMATION); 
        						        a.show(); 
        								buyTable.setItems(FXCollections.observableArrayList(customer.itemsOwned()));
        							});
        						}
        						if(boxIndex == message.number) {
        							Platform.runLater(()->{
        								bidHistoryText.setText(items.get(message.number).bidHistory);
        								bidText.clear();
        								bidText.setEditable(false);
            							bidButton.setDisable(true);
            							if(items.get(message.number).owner.username.equals("")) {
            								bidText.setPromptText("Auction over with no bids :(");
            							} else
            							bidText.setPromptText("Auction over! Winner: " + remove.owner.username);
        							});
        						}
        						break;
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
    	this.customer = customer;
    	GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Message message = new Message("user",gson.toJson(customer),clientID);
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
    
    private class myTimer extends AnimationTimer {		//displays and updates remaining time for current item
		
		@Override
		public void handle(long now) {
			for(int i=0; i<items.size(); i++) {
				if(items.get(i).timeRemaining > 0) {
					long elapsedTime = System.currentTimeMillis() - items.get(i).startTime;
					long elapsedSeconds = elapsedTime / 1000;
					items.get(i).timeRemaining = items.get(i).time - elapsedSeconds;
				}
			}
			if(boxIndex<items.size() && boxIndex>=0) {
				timeText.setText(
						Long.toString((items.get(boxIndex).timeRemaining)/60) + ":" + 
						Long.toString(((items.get(boxIndex).timeRemaining)%60)/10) + 
						Long.toString(((items.get(boxIndex).timeRemaining)%60)%10));
			}
		}
    }
}
