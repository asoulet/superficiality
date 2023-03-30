package mbg;

import model.MultipleBipartiteGraphGenerator;
import statistics.KBProfiler;
import statistics.KBProfiler.KGFormat;
import statistics.KBStatistics.StatisticsType;

public class KBProfilerTest {

	public static void main(String[] args) {
		KBProfiler profiler = new KBProfiler(MBG.getProperty("statistics_path"), KG.WIKIDATA_2016, 4, KGFormat.TTL, StatisticsType.OUT);
//		KBProfiler profiler = new KBProfiler(MBG.getProperty("statistics_path"), KG.WIKIDATA_2017, 4, KGFormat.TTL, StatisticsType.OUT);
//		KBProfiler profiler = new KBProfiler(MBG.getProperty("statistics_path"), KG.WIKIDATA_2018, 4, KGFormat.TTL, StatisticsType.OUT);
//		KBProfiler profiler = new KBProfiler(MBG.getProperty("statistics_path"), KG.WIKIDATA_2019, 4, KGFormat.TTL, StatisticsType.OUT);
//		KBProfiler profiler = new KBProfiler(MBG.getProperty("statistics_path"), KG.WIKIDATA_2020, 4, KGFormat.RDFfile, StatisticsType.OUT);
//		KBProfiler profiler = new KBProfiler(MBG.getProperty("statistics_path"), KG.WIKIDATA_2021, 4, KGFormat.RDFfile, StatisticsType.OUT);
//		KBProfiler profiler = new KBProfiler(MBG.getProperty("statistics_path"), KG.BNF, 4, KGFormat.RDF, StatisticsType.IN);
//		KBProfiler profiler = new KBProfiler(MBG.getProperty("statistics_path"), KG.CHEMBL, 4, KGFormat.RDF, StatisticsType.OUT);
//		KBProfiler profiler = new KBProfiler(MBG.getProperty("statistics_path"), KG.WIKIDATA, 10, KGFormat.RDF, StatisticsType.IN);
		profiler.analyzeDegree();
		profiler.analyzeGlobalDegree().getDistribution().show();
		MultipleBipartiteGraphGenerator generator = new MultipleBipartiteGraphGenerator(profiler, profiler.analyzeGlobalDegree());
		generator.generate();
	}

}
