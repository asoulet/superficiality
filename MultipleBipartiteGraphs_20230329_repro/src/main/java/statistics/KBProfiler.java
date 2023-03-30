package statistics;

import org.apache.log4j.Logger;

import kb.RDFReader;
import kb.TTLShallowReader;
import kb.TripleConsumer;
import mbg.KG;
import mbg.MBG;
import statistics.KBStatistics.StatisticsType;

public class KBProfiler {

	private static Logger logger = Logger.getLogger(KBProfiler.class);

	private String path;
	private KG kg;
	private int sliceNumber;
	private KGFormat format = KGFormat.RDF;
	private StatisticsType type;

	public enum KGFormat {
		RDF, TTL, TTLs, RDFfile
	}

	public KBProfiler(String path, KG kg, int sliceNumber, KGFormat format, StatisticsType type) {
		this.path = path;
		this.kg = kg;
		this.sliceNumber = sliceNumber;
		this.format = format;
		this.type = type;
	}
	
	public KG getKG() {
		return kg;
	}

	private void read(TripleConsumer statistics) {
		switch (format) {
		case RDF:
			RDFReader rdf = new RDFReader(statistics);
			rdf.load(MBG.getProperty("data_path") + kg.dataset);
			break;
		case RDFfile:
			RDFReader rdff = new RDFReader(statistics);
			rdff.loadFile(MBG.getProperty("data_path") + kg.dataset);
			break;
		case TTL:
			TTLShallowReader ttl = new TTLShallowReader(statistics);
			ttl.load(MBG.getProperty("data_path") + kg.dataset);
			break;
		case TTLs:
			TTLShallowReader ttls = new TTLShallowReader(statistics);
			ttls.loadPath(MBG.getProperty("data_path") + kg.dataset);
			break;
		}
	}

	public DegreeAnalyzer analyzeGlobalDegree() {
		String name = path + kg + "_DEG_" + type + ".kbs";
		logger.trace("check " + name);
		KBStatistics statistics = KBStatistics.load(name);		
		if (statistics == null) {
			logger.trace("compute " + name);
			statistics = new KBStatistics(kg.prefix, (type == StatisticsType.IN ? StatisticsType.IN_DEG : StatisticsType.OUT_DEG));
			read(statistics);
			logger.trace("save " + name + " with " + statistics.getTotal());
			statistics.save(name);
		}
		if (type == StatisticsType.IN)
			return statistics.getGlobalInDegree();
		else
			return statistics.getGlobalOutDegree();
	}
	
	public void analyzeDegree() {
		analyzeGlobalDegree();
		for (int i = 0; i < sliceNumber; i++) {
			String name = path + kg + "_" + type + "_" + i + "-" + sliceNumber + ".kbs";
			logger.trace("check " + name);
			KBStatistics statistics = KBStatistics.load(name);		
			if (statistics == null) {
				logger.trace("compute " + name);
				statistics = new KBStatistics(kg.prefix, type, sliceNumber, i);
				read(statistics);
				logger.trace("save " + name + " with " + statistics.getTotal());
				statistics.save(name);
			}
		}
	}
	
	public KBRelationStatistics analyzeRelation() {
		String name = path + kg + "_REL_" + type + ".kbs";
		logger.trace("check " + name);
		KBRelationStatistics statistics = KBRelationStatistics.load(name);		
		if (statistics == null) {
			logger.trace("compute " + name);
			statistics = new KBRelationStatistics(kg.prefix, type);
			read(statistics);
			logger.trace("save " + name + " with " + statistics.getTotal());
			statistics.save(name);
		}
		return statistics;
	}

	public int getSliceNumber() {
		return sliceNumber;
	}
	
	public KBStatistics getSlice(int i) {
		String name = path + kg + "_" + type + "_" + i + "-" + sliceNumber + ".kbs";
		KBStatistics statistics = KBStatistics.load(name);		
		if (statistics == null) {
			statistics = new KBStatistics(kg.prefix, type, sliceNumber, i);
			read(statistics);
			statistics.save(name);
		}
		return statistics;
	}

	public StatisticsType getType() {
		return type;
	}
	

}
