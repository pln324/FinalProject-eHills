package final_exam;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

class Server extends Observable {

	static ArrayList<Item> items;
	static Map <String,Customer> users;
	private Key publicKey;
	private Key privateKey;
	private SecretKey key;
	
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
		//generateAsymmetricKeys();
		generateSymmetricKey();
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
			sendPublicKey();
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
		Message message = decrypt(gson.fromJson(input, Message.class));
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
				this.notifyObservers(gson.toJson(encrypt(item)));
				break;
			case "user":
				Customer tempCust = gson.fromJson(message.input, Customer.class);
				Message validUser;
				if(users.containsKey(tempCust.username)) {
					if(((users.get(tempCust.username)).password).equals(tempCust.password)) {
						validUser = new Message("validUser",gson.toJson(users.get(tempCust.username)),1);
						//users.put(tempCust.username + tempCust.password, tempCust);
						this.setChanged();
						this.notifyObservers(gson.toJson(encrypt(validUser)));
						break;
					}
					else {
						Message invalidUser = new Message("invalidUser",gson.toJson(tempCust),1);
						this.setChanged();
						this.notifyObservers(gson.toJson(encrypt(invalidUser)));
						break;
					}
				}
				else {
					users.put(tempCust.username, tempCust);
					validUser = new Message("validUser",gson.toJson(tempCust),1);
					this.setChanged();
					this.notifyObservers(gson.toJson(encrypt(validUser)));
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
	
	 private void generateSymmetricKey() {
	    	KeyGenerator keygen;
			try {
				keygen = KeyGenerator.getInstance("AES");
				SecureRandom random = new SecureRandom();
		    	keygen.init(random);
		    	key = keygen.generateKey();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
	    }
	 private Message decrypt(Message message) {
		 try {
			 Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			 cipher.init(Cipher.DECRYPT_MODE, key);
			 Gson gson = new Gson();
			 Type ItemListType = new TypeToken<byte[]>(){}.getType(); 
			 byte[] decipheredText = cipher.doFinal(gson.fromJson(message.input, ItemListType));
			 return gson.fromJson(new String(decipheredText), Message.class);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 return null;
	 }
	 
	 public Message encrypt(Message message) {
	    	try {
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				cipher.init(Cipher.ENCRYPT_MODE, key);
				GsonBuilder builder = new GsonBuilder();
				Gson gson = builder.create();
				byte[] input = gson.toJson(message).getBytes();	  
			    cipher.update(input);
			    byte[] cipherText = cipher.doFinal();
			    return new Message("encrypt",gson.toJson(cipherText),1);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return null;
	    }
	 
	 private void generateAsymmetricKeys() {
		 	KeyPairGenerator pairgen;
			try {
				pairgen = KeyPairGenerator.getInstance("RSA");
				SecureRandom random = new SecureRandom();
		    	pairgen.initialize(2048, random);
		    	KeyPair keyPair = pairgen.generateKeyPair();
		    	publicKey = keyPair.getPublic();
		    	privateKey = keyPair.getPrivate();
			} catch (NoSuchAlgorithmException e) {
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
	
	public void sendPublicKey() {
		Gson gson = new Gson();
		String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
		Message message = new Message("key",encodedKey,1);
		this.setChanged();
		this.notifyObservers(gson.toJson(message));
	}
}