package final_exam;

public class Item {
	public String name;
	public String description;
	public double minPrice;
	public int time;
	public Customer owner;
	public long timeRemaining;
	public boolean sold;
	public long startTime;
	public String bidHistory;
	public String image;
	public double buyItNow;
	
	public Item() {
		name = "";
		description = "";
		minPrice = 0;
		time = 0;
		timeRemaining = 0;
		sold = true;
		owner = new Customer();
		buyItNow = 0;
		image = "";
	}
	
	public Item(String name, String description, double minPrice, int time, String image, double buyItNow) {
		this.name = name;
		this.description = description;
		this.minPrice = minPrice;
		this.time = time;
		timeRemaining = time;
		sold = false;
		owner = new Customer();
		this.image = image;
		bidHistory = "Item Listed\n";
		this.buyItNow = buyItNow;
		startTime = System.currentTimeMillis() - startTime;
		timer();
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Double getMinPrice() {
		return (minPrice);
	}
	
	public void timer() {
		Thread timer = new Thread(()-> {
			while (timeRemaining>0) {
				long elapsedTime = System.currentTimeMillis() - startTime;
				long elapsedSeconds = elapsedTime / 1000;
				timeRemaining = time - elapsedSeconds;
			}
		});
		timer.start();
	}
}

