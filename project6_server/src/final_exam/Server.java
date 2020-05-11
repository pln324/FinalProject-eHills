package final_exam;
import java.io.File;
import java.io.FileNotFoundException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class Server extends Observable {

	static ArrayList<Item> items;
	static Map <String,Customer> users;
	
	public static void main(String[] args) throws FileNotFoundException{
		users = new HashMap<String,Customer>();
		items = new ArrayList<Item>();
		parseArgs(args);
		new Server().runServer();
	}

	public static void parseArgs(String[] args) throws FileNotFoundException {
		 Scanner sc = new Scanner(new File(args[0]));
		 String[] line = {};
	        while(sc.hasNextLine()) {// each line is an item, each aspect is separated by commas;
	        	line = sc.nextLine().split(";");
	        	items.add(new Item(line[0],line[1],Double.parseDouble(line[2]),Integer.parseInt(line[3])));
	        }
	        sc.close();
	}

	private void runServer() {
		try {
			setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private boolean timerStarted = false;
	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {											//thread to wait for new clients
			Socket clientSocket = serverSock.accept();
			System.out.println("Connecting to... " + clientSocket);
			ClientHandler handler = new ClientHandler(this, clientSocket);
			this.addObserver(handler);
			Thread t = new Thread(handler);
			Thread sold = new Thread(()-> {						//thread that waits for timer to hit 0 on items	
				while (true) {
					int i=0;
					for (Iterator<Item> it = items.iterator(); it.hasNext(); i++) {
					    Item remove = it.next();
					    if (remove.timeRemaining == 0 && remove.sold == false) { 
					    	remove.sold = true;
					    	GsonBuilder builder = new GsonBuilder();
							Gson gson = builder.create();
							if(users.containsKey(remove.owner.username)) {
								users.get(remove.owner.username).itemPurchased(remove);
								items.get(i).bidHistory += items.get(i).owner.username + 
        								" won the auction with a bid of " + Double.toString(items.get(i).minPrice);
							}
							else {
								items.get(i).bidHistory += "Auction over with no bids.";
							}
							Message message = new Message("remove",gson.toJson(remove),i);
							this.setChanged();
							this.notifyObservers(gson.toJson(message));
					    	break;
						} 
					} 
				}
			});
			t.start();
			addItems();
			if(timerStarted == false) {
				timerStarted = true;
				sold.start();
			}
		}
	}
	
	protected void processRequest(String input) {
		Gson gson = new Gson();
		Message item;
		Message message = gson.fromJson(input, Message.class);
		try {
			switch (message.type) {
			case "bid":					//bid received for item
				Item tempy = gson.fromJson(message.input, Item.class);
				items.set(message.number, tempy);
				items.get(message.number).timer();
				item = new Message("item",gson.toJson(items.get(message.number)),message.number);
				this.setChanged();
				this.notifyObservers(gson.toJson(item));
				break;
			case "user":				//a user is attempting to log in
				Customer tempCust = gson.fromJson(message.input, Customer.class);
				Message validUser;
				if(users.containsKey(tempCust.username)) {													//checks if user has been registered before
					if(((users.get(tempCust.username)).password).equals(tempCust.password)) {				//if passwords match
						validUser = new Message("validUser",gson.toJson(users.get(tempCust.username)),1);	//send a message okaying user
						this.setChanged();
						this.notifyObservers(gson.toJson(validUser));
						break;
					}
					else {
						Message invalidUser = new Message("invalidUser",gson.toJson(tempCust),1);			//send a message denying user
						this.setChanged();
						this.notifyObservers(gson.toJson(invalidUser));
						break;
					}
				}
				else {
					users.put(tempCust.username, tempCust);								//user does not exist, add to existing users
					validUser = new Message("validUser",gson.toJson(tempCust),1);
					this.setChanged();
					this.notifyObservers(gson.toJson(validUser));
				}
				break;
			case "purchase":													//when item has been sold to a customer
				tempCust = gson.fromJson(message.input, Customer.class);
				users.put(tempCust.username, tempCust);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	public void addItems() {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Message message = new Message("itemArray",gson.toJson(items),1);
		this.setChanged();
		this.notifyObservers(gson.toJson(message));
	}
	
}