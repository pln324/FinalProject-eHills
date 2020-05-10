package final_exam;
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
		bought = new ArrayList<Item>();
	}
	
	public void itemPurchased(Item item) {
		bought.add(item);
	}
	
	public ArrayList<Item> itemsOwned() {
		return bought;
	}
}
