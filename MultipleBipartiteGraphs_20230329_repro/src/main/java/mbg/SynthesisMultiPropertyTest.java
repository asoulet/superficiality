package mbg;

import java.util.ArrayList;

import model.MultipleBipartiteGraphGenerator;
import model.SynthesisProperty;
import statistics.Distribution;

public class SynthesisMultiPropertyTest {

	public static int NUMBER = 25;
	public static int COUNT = 20000;
	public static double BETA = 0.85;
	public static double ALPHA = 1;
	public static double SUPERFICIALITY = 0.05;
	public static boolean CHECK = false;

	public static void main(String[] args) {
		ArrayList<SynthesisProperty> properties = new ArrayList<SynthesisProperty>();
		for (int i = 0; i < NUMBER; i++)
			properties.add(new SynthesisProperty(COUNT, 1 - BETA, ALPHA));
		Distribution distribution = MultipleBipartiteGraphGenerator.generate(properties, SUPERFICIALITY);
		System.out.println("Distribution:");
		distribution.show();
		if (CHECK)
			check(distribution);
	}

	private static void check(Distribution distribution) {
		double r = 1;
		double c = 0;
		for (int i = 0; i < distribution.size(); i++) {
			r = r * getK(i) / (1 + getK(i + 1));
			c += r;
			System.out.println(distribution.getValue(i) + "\t" + distribution.getCount(i) + "\t"
					+ ((double) distribution.getCount(i)) / distribution.getCount() + "\t" + r + "\t" + c + "\t"
					+ (1 - Math.pow(1. - SUPERFICIALITY, i + 1)));
		}
	}

	private static double getK(int i) {
		if (i == 0)
			return 1;
		return ((1. - SUPERFICIALITY) / (SUPERFICIALITY - 1. / NUMBER)) * (1. - ((double) i) / NUMBER);
	}

}
