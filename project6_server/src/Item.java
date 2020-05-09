
public class Item {
	public String name;
	public String description;
	public double minPrice;
	public int time;
	public Customer owner;
	public long timeRemaining;
	
	public Item() {
		name = "";
		description = "";
		minPrice = 0;
		time = 0;
		timeRemaining = 0;
		owner = new Customer();
	}
	
	public Item(String name, String description, double minPrice, int time) {
		this.name = name;
		this.description = description;
		this.minPrice = minPrice;
		this.time = time;
		timeRemaining = time;
	}
}
