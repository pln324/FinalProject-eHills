
public class Item {
	public String description;
	public double minPrice;
	public int time;
	
	public Item() {
		description = "";
		minPrice = 0;
		time = 0;
	}
	
	public Item(String description, double minPrice, int time) {
		this.description = description;
		this.minPrice = minPrice;
		this.time = time;
	}
}
