import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

class Server extends Observable {

	static ArrayList<Item> items;
	public static void main(String[] args) {
		items = new ArrayList<Item>();
		for(int i=0; i<5; i++) {
			Item i1 = new Item("item" + Integer.toString(i),"an item",250,300);
			items.add(i1);
		}
		new Server().runServer();
	}

	private void runServer() {
		try {
			setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			System.out.println("Connecting to... " + clientSocket);
			ClientHandler handler = new ClientHandler(this, clientSocket);
			this.addObserver(handler);
			Thread t = new Thread(handler);
			t.start();
			addItems();
		}
	}

	protected void processRequest(String input) {
		String output = "Error";
		Gson gson = new Gson();
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
				items.get(message.number).minPrice = tempy.minPrice;
				Message item = new Message("item",gson.toJson(items.get(message.number)),message.number);
				this.setChanged();
				this.notifyObservers(gson.toJson(item));
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