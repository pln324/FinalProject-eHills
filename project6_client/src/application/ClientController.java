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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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
    private String customerID;
    private String password;
    
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
    private Button quitButton;
    
    public ClientController() {
    	items = new ArrayList<Item>();
    	itemsBox = new ChoiceBox<String>();
    	names = new ArrayList<String>();
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
            	timeText.setText(Integer.toString(temp.time));
            	lowestBidText.setText(Double.toString(temp.minPrice));
            } 
        }); 
    }
    
    public void bidButtonPressed() {
    	bidText.setPromptText("How much?");
		try {
			double bid = Double.parseDouble(bidText.getText());
			if(bid>items.get(boxIndex).minPrice) {
				items.get(boxIndex).minPrice = bid;
				lowestBidText.setText(bidText.getText());
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
    
    public void quitButtonPressed() {
    	System.exit(0);
    }
//    public void itemsBoxUsed() {
//    	Item temp = items.get(itemsBox.getSelectionModel().getSelectedIndex());
//    	descriptionText.setText(temp.description);
//    	timeText.setText(Integer.toString(temp.time));
//    	lowestBidText.setText(Double.toString(temp.minPrice));
//    }
    
    public void setID(String id, String password) {
    	customerID = id;
    	this.password = password;
    	
    }
    
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
        						Item tempy = gson.fromJson(message.input, Item.class);
        						items.get(message.number).minPrice = tempy.minPrice;
        						lowestBidText.setText(Double.toString(tempy.minPrice));
        						//names.add(tempy.name);
        						//itemsBox.setItems(FXCollections.observableArrayList(names));
        						break;
        					case "itemArray":
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
        					System.out.println("From server: " + input);
        					processRequest(input);
        					//wait();
        				}
        			} catch (Exception e) {
        				e.printStackTrace();
        		}
        	}
        });

//        Thread writerThread = new Thread(() ->{
//            while (true) {
//            	if(changed==true) {
//            		changed = false;
//            		//String input = consoleInput.nextLine();
//            		//String[] variables = input.split(",");
//            		//Message request = new Message(variables[0], variables[1], Integer.valueOf(variables[2]));
//            		GsonBuilder builder = new GsonBuilder();
//            		Gson gson = builder.create();
//            		sendToServer(gson.toJson(info));
//            	}
//            }
//          });
        readerThread.start();
        //writerThread.start();
      }
    protected void processRequest(String input) {
        return;
      }
    protected void sendToServer(String string) {
        System.out.println("Sending to server: " + string);
        toServer.println(string);
        toServer.flush();
      }
}
