package application;

public class Item {
	public String name;
	public String description;
	public double minPrice;
	public int time;
	public Customer owner;
	public long timeRemaining;
	private long startTime;
	
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

