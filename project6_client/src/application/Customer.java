package application;

import java.util.ArrayList;

public class Customer {
	public String username;
	public String password;
	public ArrayList<Item> bought;
	
	public Customer() {
		username = "";
		password = "";
		bought = new ArrayList<Item>();
	}
	
	public Customer(String username, String password) {
		this.username = username;
		this.password = password;
	}
}
