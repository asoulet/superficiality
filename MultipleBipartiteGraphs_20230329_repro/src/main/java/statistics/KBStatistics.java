package statistics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import kb.TripleConsumer;

public class KBStatistics implements TripleConsumer, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(KBStatistics.class);

	private String prefix = null;
	private StatisticsType type = StatisticsType.IN_OUT_DEG;
	private HashMap<String, DegreeAnalyzer> inDegrees = new HashMap<String, DegreeAnalyzer>();
	private HashMap<String, DegreeAnalyzer> outDegrees = new HashMap<String, DegreeAnalyzer>();
	private DegreeAnalyzer globalInDegree = new DegreeAnalyzer("in-degree");
	private DegreeAnalyzer globalOutDegree = new DegreeAnalyzer("out-degree");
	private int sliceNumber = 1;
	private int sliceIndex = 0;
	private long total = 0;

	public enum StatisticsType {
		IN, OUT, IN_OUT_DEG, DEG, IN_DEG, OUT_DEG
	}

	public KBStatistics(String prefix, StatisticsType type) {
		this(prefix, type, 1, 0);
	}

	public KBStatistics(String prefix, StatisticsType type, int sliceNumber, int sliceIndex) {
		this.prefix = prefix;
		this.type = type;
		this.sliceNumber = sliceNumber;
		this.sliceIndex = sliceIndex;
	}

	@Override
	public void begin() {
	}

	@Override
	public void end() {
		for (Entry<String, DegreeAnalyzer> pd : inDegrees.entrySet()) {
			pd.getValue().compute();
			pd.getValue().free();
		}
		for (Entry<String, DegreeAnalyzer> pd : outDegrees.entrySet()) {
			pd.getValue().compute();
			pd.getValue().free();
		}
		globalInDegree.compute();
		globalInDegree.free();
		globalOutDegree.compute();
		globalOutDegree.free();
	}

	public void show() {
		System.out.println("In-degrees:");
		for (Entry<String, DegreeAnalyzer> pd : inDegrees.entrySet()) {
			DegreeAnalyzer da = pd.getValue();
			System.out.println(pd.getKey() + "\t" + da);
			/*
			 * Distribution dist = new Distribution(da.getSample()); dist.show(); double
			 * alpha = 1 + 1 / (1 - ((double)da.getDistinct()) / da.getCount()); double exp
			 * = Math.pow(2, 1 / alpha); System.out.println("median: " + dist.getMedian() +
			 * " alpha:" + alpha + " expected-median:" + exp); int missing =
			 * dist.getMissing(); System.out.println("Missing: " + missing);
			 */
		}
		System.out.println("Out-degrees:");
		for (Entry<String, DegreeAnalyzer> pd : outDegrees.entrySet())
			System.out.println(pd.getKey() + "\t" + pd.getValue());
		System.out.println("Global in-degrees:\t" + globalInDegree);
		System.out.println("Global out-degrees:\t" + globalOutDegree);
	}

	private long nb = 0;
	
	@Override
	public void consume(String s, String p, String o) {
//		System.out.println(s + "\t" + p + "\t" + o);
		if (nb++ % 1000000 == 0) {
			System.out.print(".");
			System.out.flush();
		}
		if ((prefix == null && s.startsWith("http") && o.startsWith("http"))
				|| (prefix != null && s.startsWith(prefix) && o.startsWith(prefix))) {
			//System.out.println(s + "\t" + p + "\t" + o);
			if (sliceNumber <= 1 || ((int) Math.abs(p.hashCode()) % sliceNumber == sliceIndex)) {
				total++;
				if (type == StatisticsType.IN || type == StatisticsType.IN_OUT_DEG)
					addInDegree(p, o);
				if (type == StatisticsType.DEG || type == StatisticsType.IN_OUT_DEG || type == StatisticsType.IN_DEG)
					globalInDegree.add(o);
				if (type == StatisticsType.OUT || type == StatisticsType.IN_OUT_DEG)
					addOutDegree(p, s);
				if (type == StatisticsType.DEG || type == StatisticsType.IN_OUT_DEG || type == StatisticsType.OUT_DEG)
					globalOutDegree.add(s);
			}
		}
	}

	public long getTotal() {
		return total;
	}

	private void addInDegree(String p, String o) {
		DegreeAnalyzer degree = inDegrees.get(p);
		if (degree == null) {
			degree = new DegreeAnalyzer(p);
		}
		degree.add(o);
		inDegrees.put(p, degree);
	}

	private void addOutDegree(String p, String s) {
		DegreeAnalyzer degree = outDegrees.get(p);
		if (degree == null) {
			degree = new DegreeAnalyzer(p);
		}
		degree.add(s);
		outDegrees.put(p, degree);
	}

	public void save(String filename) {
		try {

			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(this);
			objectOut.close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	public static KBStatistics load(String filename) {
		try {

			FileInputStream fileIn = new FileInputStream(filename);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			Object obj = objectIn.readObject();
			objectIn.close();
			return (KBStatistics) obj;

		} catch (Exception e) {
			return null;
		}
	}

	public DegreeAnalyzer getGlobalInDegree() {
		return globalInDegree;
	}

	public DegreeAnalyzer getGlobalOutDegree() {
		return globalOutDegree;
	}

	public int getEdgeNumber() {
		return globalInDegree.getCount();
	}

	public int getPropertyNumber() {
		return inDegrees.size();
	}

	public HashMap<String, DegreeAnalyzer> getInDegrees() {
		return inDegrees;
	}

	public HashMap<String, DegreeAnalyzer> getOutDegrees() {
		return outDegrees;
	}

}
