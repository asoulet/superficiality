package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

import statistics.DegreeAnalyzer;
import statistics.Distribution;
import statistics.KBProfiler;
import statistics.KBStatistics.StatisticsType;

public class MultipleBipartiteGraphGenerator {
	
	private static Logger logger = Logger.getLogger(MultipleBipartiteGraphGenerator.class);

	
	private int total = 0;
	private double pNew = 0;
	private int distinct = 0;
	private KBProfiler profiler = null;


	private int propertyNumber;

	public MultipleBipartiteGraphGenerator(KBProfiler profiler, DegreeAnalyzer globalDegree) {		
		this.profiler = profiler;
		this.total = globalDegree.getCount();
		this.distinct = globalDegree.getDistinct();
		this.propertyNumber = 0;
		double pOther = 0;
		for (int i = 0; i < profiler.getSliceNumber(); i++) {
			HashMap<String, DegreeAnalyzer> properties = getProperties(i);
			for (Entry<String, DegreeAnalyzer> pd : properties.entrySet()) {				
				double count = pd.getValue().getCount();
				double distinct = pd.getValue().getDistinct();
				pOther += (count / total) * (distinct / count);
				propertyNumber++;
			}
		}
		pNew = distinct / (pOther * total);
		logger.info("node number= " + distinct);
		logger.info("edge number= " + total);
		logger.info("property number= " + propertyNumber);
		//logger.info("p_other= " + pOther);
		logger.info("p_new= " + pNew);
//		logger.info(pOther * (1 - pNew) * total);
//		logger.info(pOther * pNew * total);
	}
	
	public static Distribution generate(ArrayList<SynthesisProperty> properties, double d) {
		int total = 0;
		int propertyNumber = 0;
		for (SynthesisProperty p : properties) {
			total += p.getCount();
			propertyNumber++;
		}
		double pOther = 0;
		for (SynthesisProperty p : properties) {
			double count = p.getCount();
			pOther += (count / total) * p.getRatio();
			//distinct += count * p.getRatio();
		}
		double pNew = d;
		int distinct = (int) (pNew * (pOther * total));
		logger.info("node number= " + distinct);
		logger.info("edge number= " + total);
		logger.info("property number= " + propertyNumber);
		logger.info("p_new= " + pNew);
		KGStorage storage = new KGFastStorage(distinct * 10);
		int k = 0;
		int edges = 0;
		for (SynthesisProperty p : properties) {
			k++;
			edges += p.getCount();
			logger.info((int)(p.getCount() * p.getRatio()) + " " + p.getCount() + " " + k + "/" + propertyNumber + " " + (int)(((double)edges) / total * 10000) / 100f + "%");
			RBTBipartiteGraphGenerator bgg = new RBTBipartiteGraphGenerator(p);
			bgg.generate(distinct, storage, pNew);
			//RBTBipartiteGraphGenerator bgg = RBTBipartiteGraphGenerator.generate(p.getCount(), p.getRatio(), p.getGamma());
			logger.info(bgg.getNodeNumber() + " " + bgg.getEdgeNumber());
			//Distribution dist =  new Distribution(bgg.getSample());
			//dist.show();
		}
		//storage.show();
		return new Distribution(storage.getSample());
	}

	private HashMap<String, DegreeAnalyzer> getProperties(int i) {
		HashMap<String, DegreeAnalyzer> properties = null;
		if (profiler.getType() == StatisticsType.IN)
			properties = profiler.getSlice(i).getInDegrees(); 
		if (profiler.getType() == StatisticsType.OUT)
			properties = profiler.getSlice(i).getOutDegrees();
		return properties;
	}
	
	public void generate() {
		long start = System.currentTimeMillis();
		//KGMemoryStorage storage = new KGMemoryStorage(distinct);
		//KGStorage storage = new KGDatabaseStorage(profiler.getKG(), profiler.getType());
		KGStorage storage = new KGFastStorage(distinct);
		int p = 0;
		int edges = 0;
		for (int i = 0; i < profiler.getSliceNumber(); i++) {
			HashMap<String, DegreeAnalyzer> properties = getProperties(i);
			for (Entry<String, DegreeAnalyzer> pd : properties.entrySet()) //if (pd.getKey().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
			{
				p++;
				edges += pd.getValue().getCount();
				logger.info(pd.getKey() + " " + pd.getValue().getDistinct() + " " + pd.getValue().getCount() + " " + p + "/" + propertyNumber + " " + (int)(((double)edges) / total * 10000) / 100f + "%");
				RBTBipartiteGraphGenerator bgg = new RBTBipartiteGraphGenerator(pd.getValue());
				//bgg.configure(Math.max(distinct, storage.getMaximum()), storage, pNew);				
				//bgg.configure(distinct, storage, pNew);				
				bgg.generate(distinct, storage, pNew);
				logger.info(pd.getKey() + " " + bgg.getNodeNumber() + " " + bgg.getEdgeNumber());
				Distribution dist =  new Distribution(bgg.getSample());
				//new Distribution(bgg.getSample()).show();
				//storage.store(bgg.getNodes());
				//bgg.store();
				//storage.show();
				ArrayList<Distribution> distributions = new ArrayList<Distribution>();
				distributions.add(pd.getValue().getDistribution());
				distributions.add(dist);
				Distribution.display(distributions);				
			}
		}
		
		logger.info("Distribution");
		Distribution distribution = new Distribution(storage.getSample());
		distribution.show();
		System.out.println((System.currentTimeMillis() - start) / 1000.);
	}
	
}
