package mbg;

import model.RBTBipartiteGraphGenerator;
import model.SynthesisProperty;
import statistics.Distribution;

public class SynthesisMonoPropertyTest {

	public static int COUNT = 20000;
	public static double BETA = 0.85;
	public static double ALPHA = 1;

	public static void main(String[] args) {
		RBTBipartiteGraphGenerator property = new RBTBipartiteGraphGenerator(new SynthesisProperty(COUNT, 1 - BETA, ALPHA));
		property.generate();
		Distribution d = new Distribution(property.getSample());
		d.show();
	}

}
