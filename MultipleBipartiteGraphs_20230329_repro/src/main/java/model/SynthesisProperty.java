package model;

public class SynthesisProperty {
	private int count;
	private double ratio;
	private double gamma;

	public SynthesisProperty(int count, double ratio, double gamma) {
		this.count = count;
		this.ratio = ratio;
		this.gamma = gamma;
	}

	public int getCount() {
		return count;
	}

	public double getRatio() {
		return ratio;
	}

	public double getGamma() {
		return gamma;
	}
	
}
