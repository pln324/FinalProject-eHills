
public class Item {
	public String name;
	public String description;
	public double minPrice;
	public int time;
	
	public Item() {
		name = "";
		description = "";
		minPrice = 0;
		time = 0;
	}
	
	public Item(String name, String description, double minPrice, int time) {
		this.name = name;
		this.description = description;
		this.minPrice = minPrice;
		this.time = time;
	}
}
