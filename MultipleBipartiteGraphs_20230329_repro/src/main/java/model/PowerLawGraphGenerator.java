package model;

import org.apache.log4j.Logger;

import statistics.DegreeAnalyzer;
import statistics.Distribution;
import statistics.KBProfiler;

public class PowerLawGraphGenerator {

	private static Logger logger = Logger.getLogger(PowerLawGraphGenerator.class);
	private int total;
	private int distinct;
	private DegreeAnalyzer globalDegree;
	
	public PowerLawGraphGenerator(KBProfiler profiler, DegreeAnalyzer globalDegree) {
		this.total = globalDegree.getCount();
		this.distinct = globalDegree.getDistinct();
		this.globalDegree = globalDegree;
		logger.info("node number= " + distinct);
		logger.info("edge number= " + total);
	}

	public void generate(boolean isDatabase) {
		long start = System.currentTimeMillis();
		RBTBipartiteGraphGenerator bgg = new RBTBipartiteGraphGenerator(globalDegree);
		bgg.generate();
		logger.info("Distribution");
		Distribution dist =  new Distribution(bgg.getSample());
		dist.show();
		System.out.println((System.currentTimeMillis() - start) / 1000.);
	}
	
}
