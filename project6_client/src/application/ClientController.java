package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ClientController {

	public boolean changed = false;
	public Message info;
	public String fromServ;
	public Client myClient;
    @FXML
    private Button bidButton;

    @FXML
    private TextField bidText;
    private PrintWriter toServer;
    private static String host = "127.0.0.1";
    private BufferedReader fromServer;
    public ClientController() {
    	try {
			setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void bidButtonPressed() {
    	String bid = bidText.getText();
    	info = new Message("bid",bid,1);
    	changed = true;
    	GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		sendToServer(gson.toJson(info));
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
        				String output = "Error";
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
        					}
        					output = "";
        					for (int i = 0; i < message.number; i++) {
        						output += temp;
        						output += " ";
        					}
        					System.out.println("From server: " + input);
        					processRequest(input);
        					fromServ=input;
        					bidText.setText(fromServ);
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
