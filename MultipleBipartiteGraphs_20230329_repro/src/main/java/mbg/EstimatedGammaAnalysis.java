package mbg;

import java.util.ArrayList;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

import model.RBTBipartiteGraphGenerator;
import statistics.Distribution;

public class EstimatedGammaAnalysis {

	public static int COUNT = 100000;
	public static double RATIO = 0.5;
	public static double GAMMA = 0.1;
	public static double STEP_NUMBER = 20;
	public static int LOOP_NUMBER = 10;

	public static double getDistributionQuality(int count, double ratio, double gamma) {
		double avgStat = 0;
		for (int k = 0; k < LOOP_NUMBER; k++) {
			RBTBipartiteGraphGenerator groundTruth = RBTBipartiteGraphGenerator.generate(count, ratio, gamma);
			int maximum = new Distribution(groundTruth.getSample()).getMaximumDegree();
			double eg = groundTruth.estimateGamma(maximum);
			RBTBipartiteGraphGenerator sim = RBTBipartiteGraphGenerator.generate(count, ratio, eg);
			KolmogorovSmirnovTest kstest = new KolmogorovSmirnovTest();
			double[] gtks = groundTruth.getSample(1);
			double[] simks = sim.getSample(1);
			if (gtks != null && simks != null && gtks.length > 1 && simks.length > 1) {
				double stat = kstest.kolmogorovSmirnovStatistic(gtks, simks);
				avgStat += stat;
			}
		}
		avgStat /= LOOP_NUMBER;
		return avgStat;
	}

	public static double getDistributionQualityKL(int count, double ratio, double gamma) {
		double avgStat = 0;
		for (int k = 0; k < LOOP_NUMBER; k++) {
			RBTBipartiteGraphGenerator groundTruth = RBTBipartiteGraphGenerator.generate(count, ratio, gamma);
			Distribution gtd = new Distribution(groundTruth.getSample()); 
			int maximum = gtd.getMaximumDegree();
			double eg = groundTruth.estimateGamma(maximum);
			RBTBipartiteGraphGenerator sim = RBTBipartiteGraphGenerator.generate(count, ratio, eg);
			Distribution simd = new Distribution(sim.getSample());
			double stat = gtd.getKLDivergence(simd);
			avgStat += stat;
		}
		avgStat /= LOOP_NUMBER;
		return avgStat;
	}

	public static double getEstimatedGamma(int count, double ratio, double gamma) {
		double g = 0;
		for (int k = 0; k < LOOP_NUMBER; k++) {
			RBTBipartiteGraphGenerator groundTruth = RBTBipartiteGraphGenerator.generate(count, ratio, gamma);
			int maximum = new Distribution(groundTruth.getSample()).getMaximumDegree();
			double eg = groundTruth.estimateGamma(maximum);
			g += eg;
		}
		g /= LOOP_NUMBER;
		return g;
	}

	public static double getMaximumRatio(int count, double ratio, double gamma) {
		ArrayList<RBTBipartiteGraphGenerator> gts = new ArrayList<RBTBipartiteGraphGenerator>();
		ArrayList<RBTBipartiteGraphGenerator> sims = new ArrayList<RBTBipartiteGraphGenerator>();
		double maximum = 0;
		for (int k = 0; k < LOOP_NUMBER; k++) {
			RBTBipartiteGraphGenerator groundTruth = RBTBipartiteGraphGenerator.generate(count, ratio, gamma);
			int m = new Distribution(groundTruth.getSample()).getMaximumDegree();
			double eg = groundTruth.estimateGamma(m);
			maximum += m;
			gts.add(groundTruth);
			sims.add(RBTBipartiteGraphGenerator.generate(count, ratio, eg));
		}
		maximum /= LOOP_NUMBER;
		// System.out.println("gt " + maximum);
		double estimated = 0;
		for (RBTBipartiteGraphGenerator s : sims) {
			int m = new Distribution(s.getSample()).getMaximumDegree();
			estimated += m;
		}
		estimated /= LOOP_NUMBER;
		// System.out.println("e " + estimated);
		return Math.abs(maximum - estimated) / maximum;
	}

	public static double getMaximumStdDev(int count, double ratio, double gamma) {
		ArrayList<RBTBipartiteGraphGenerator> gts = new ArrayList<RBTBipartiteGraphGenerator>();
		double maximum = 0;
		for (int k = 0; k < LOOP_NUMBER; k++) {
			RBTBipartiteGraphGenerator groundTruth = RBTBipartiteGraphGenerator.generate(count, ratio, gamma);
			int m = new Distribution(groundTruth.getSample()).getMaximumDegree();
			maximum += m;
			gts.add(groundTruth);
		}
		maximum /= LOOP_NUMBER;
		double stddev = 0;
		for (RBTBipartiteGraphGenerator gt : gts)
			stddev += Math.abs(maximum - gt.getMaximum()) / LOOP_NUMBER;
		stddev /= LOOP_NUMBER;
		return stddev;
	}

	public static void main(String[] args) {
		double max = 0;
		for (int r = 0; r <= STEP_NUMBER; r++) {
			double ratio = 1. / STEP_NUMBER * r;
			System.out.print(ratio + "\t");
			for (int g = 0; g <= STEP_NUMBER; g++) {
				double gamma = 1. / STEP_NUMBER * g;
				double quality = getDistributionQualityKL(COUNT, ratio, gamma);
				System.out.print( quality + "\t");
				max = Math.max(quality, max);
			}
			System.out.println();
		}
		System.out.println(max);
	}

}
