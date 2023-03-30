package model;

import java.util.ArrayList;
import org.apache.log4j.Logger;

import mbg.MBG;
import model.Node.Origin;
import statistics.DegreeAnalyzer;

public class RBTBipartiteGraphGenerator {

	private static Logger logger = Logger.getLogger(RBTBipartiteGraphGenerator.class);

	protected int count;
	protected double ratio;

	protected double gamma;

	protected int maximum = 0;
	private KGStorage storage;
	private RedBlackTree tree = null;


	public RBTBipartiteGraphGenerator(DegreeAnalyzer da) {
		this.count = da.getCount();
		this.ratio = ((double) da.getDistinct() - 1) / ((double) da.getCount() - 1);
		if (da.getCount() == 1)
			ratio = 1;
		this.maximum = da.getMaximum();
		if (MBG.EXPONENT_PARAMETRIZING)
			gamma = estimateGamma(0, 1);
		else
			gamma = 1;
		this.count = (int)(da.getCount() * MBG.SAMPLE);
		logger.info("gamma= " + gamma + " ratio= " + ratio);
		tree = new RedBlackTree(gamma);
	}
	
	public RBTBipartiteGraphGenerator(SynthesisProperty sp) {
		this.count = sp.getCount();
		this.ratio = sp.getRatio();
		if (MBG.EXPONENT_PARAMETRIZING)
			this.gamma = sp.getGamma();
		else
			this.gamma = 1;
		tree = new RedBlackTree(gamma);
	}

	// estimate sublinear gamma attachment with maximum approximate
	public double estimateGamma(double min, double max) {
		double middle = (min + max) / 2;
		if (max - min < 0.0001)
			return middle;
		double maximumEstimatedDegree = Math
				.pow((1 - middle) * (1 - ratio) * Math.pow(ratio, middle - 1) * Math.log(count) + 1, 1 / (1 - middle));
		if (maximumEstimatedDegree < maximum)
			return estimateGamma(middle, max);
		else
			return estimateGamma(min, middle);
	}
	
	public double estimateGamma(int max) {
		this.maximum = max;
		return estimateGamma(0, 1);
	}
	
	public void generate() {
		generate(count * 10, null, 0);
	}


	public void generate(int distinct, KGStorage storage, double pNew) {
		this.storage = storage;
		for (int i = 0; i < count; i++) {
			if (MBG.VERBOSE_MODE)
				if (i % 100000 == 0) {
					if (i % 1000000 == 0)
						System.out.print("o");
					else
						System.out.print(".");
					System.out.flush();
				}
			int range = (int) ((distinct - 0) * ((i + 1.) / count) + 0); // avant +10 à la fin
			if (tree.getNodeNumber() < 1 || Math.random() < ratio)
				if (Math.random() < pNew)
					insertNewNode(range);
				else {
						insertOtherNode(range);
				}
			else
				insertNewEdge();
		}
		if (MBG.VERBOSE_MODE)
			System.out.println();
		if (storage != null)
			store();
	}

	private void insertNewEdge() {
		tree.drawEdge();
	}

	private void insertNewNode(int nodeNumber) {
		int currentNewNode = storage.getNewNode(tree.getMaximum() + 1);
		tree.insert(currentNewNode, Origin.NEW);
	}

	private void insertOtherNode(int nodeNumber) {
		tree.drawOtherNode(nodeNumber);
	}
	
	public ArrayList<Integer> getSample() {
		return tree.getSample();
	}
	
	public double getRatio() {
		return ratio;
	}

	public double getGamma() {
		return gamma;
	}

	public int getMaximum() {
		return maximum;
	}

	public int getEdgeNumber() {
		return count;
	}

	public void store() {
		tree.apply(new RBTConsumer() {
			
			@Override
			public void consume(model.RedBlackTree.Node node) {
				storage.storeNode(node.id, node.count, node.origin);
			}
		});
	}
	
	public void store(KGStorage s) {
		this.storage = s;
		tree.apply(new RBTConsumer() {
			
			@Override
			public void consume(model.RedBlackTree.Node node) {
				storage.storeNode(node.id, node.count, node.origin);
			}
		});
	}
	
	public static RBTBipartiteGraphGenerator generate(int count, double ratio, double gamma) {
		RBTBipartiteGraphGenerator property = new RBTBipartiteGraphGenerator(new SynthesisProperty(count, ratio, gamma));
		property.generate();
		return property;
	}

	public int getNodeNumber() {
		return tree.getNodeNumber();
	}

	public double[] getSample(int i) {
		ArrayList<Integer> array = getSample();
		double [] sample = new double [array.size()];
		int k = 0;
		for (int j : array)
			sample[k++] = j;
		return sample;
	}



}
