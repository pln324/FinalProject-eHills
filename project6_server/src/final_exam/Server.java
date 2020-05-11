package final_exam;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;

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
import com.google.gson.reflect.TypeToken;

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
		
		while (true) {
			Socket clientSocket = serverSock.accept();
			System.out.println("Connecting to... " + clientSocket);
			ClientHandler handler = new ClientHandler(this, clientSocket);
			this.addObserver(handler);
			Thread t = new Thread(handler);
			Thread sold = new Thread(()-> {
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

//	public void updateTimes(long time) {
//		for (int i=0; i<items.size(); i++) {
//			items.get(i).timeRemaining = items.get(i).time - time;
//			if(items.get(i).timeRemaining<0) {
//				items.get(i).timeRemaining = 0;
//			}
//			GsonBuilder builder = new GsonBuilder();
//			Gson gson = builder.create();
//			Message message = new Message("item",gson.toJson(items.get(i)),i);
//			this.setChanged();
//			this.notifyObservers(gson.toJson(message));
//		}
//	}
	
	protected void processRequest(String input) {
		String output = "Error";
		Gson gson = new Gson();
		Message item;
		Message message = gson.fromJson(input, Message.class);
		try {
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
			case "bid":
//				Type ItemListType = new TypeToken<ArrayList<Item>>(){}.getType(); 
//				items = gson.fromJson(message.input, ItemListType);
				Item tempy = gson.fromJson(message.input, Item.class);
				//items.get(message.number).minPrice = tempy.minPrice;
				//items.get(message.number).owner = tempy.owner;
				items.set(message.number, tempy);
				items.get(message.number).timer();
				//items.get(message.number).timeRemaining = tempy.timeRemaining;
				item = new Message("item",gson.toJson(items.get(message.number)),message.number);
				this.setChanged();
				this.notifyObservers(gson.toJson(item));
				break;
			case "user":
				Customer tempCust = gson.fromJson(message.input, Customer.class);
				Message validUser;
				if(users.containsKey(tempCust.username)) {
					if(((users.get(tempCust.username)).password).equals(tempCust.password)) {
						validUser = new Message("validUser",gson.toJson(users.get(tempCust.username)),1);
						//users.put(tempCust.username + tempCust.password, tempCust);
						this.setChanged();
						this.notifyObservers(gson.toJson(validUser));
						break;
					}
					else {
						Message invalidUser = new Message("invalidUser",gson.toJson(tempCust),1);
						this.setChanged();
						this.notifyObservers(gson.toJson(invalidUser));
						break;
					}
				}
				else {
					users.put(tempCust.username, tempCust);
					validUser = new Message("validUser",gson.toJson(tempCust),1);
					this.setChanged();
					this.notifyObservers(gson.toJson(validUser));
				}
				break;
			case "purchase":
				tempCust = gson.fromJson(message.input, Customer.class);
				users.put(tempCust.username, tempCust);
				break;
			}
			//addItems();
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