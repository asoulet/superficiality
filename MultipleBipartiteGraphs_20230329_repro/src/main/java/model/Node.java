package model;

public class Node {

	private int counter = 0;
	private Origin origin = null;
	
	public enum Origin {OTHER, NEW};
	
	public Node(int counter, Origin origin) {
		this.counter = counter;
		this.origin = origin;
	}
	
	public void add(int value) {
		counter += value;
	}

	public void add(int value, Origin origin) {
		counter += value;
		if (origin == Origin.NEW) {
			this.origin = Origin.NEW;
		}
	}

	public int getCounter() {
		return counter;
	}

	public Origin getOrigin() {
		return origin;
	}
	
	
}
