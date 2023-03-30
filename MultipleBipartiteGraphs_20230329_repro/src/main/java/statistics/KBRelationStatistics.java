package statistics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import kb.TripleConsumer;
import statistics.KBStatistics.StatisticsType;

public class KBRelationStatistics implements TripleConsumer, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(KBRelationStatistics.class);

	private String prefix = null;
	private StatisticsType type = null;
	private HashMap<Integer, HashSet<Integer>> entities = new HashMap<Integer, HashSet<Integer>>();
	private long nb = 0;
	private int total = 0;
	private Distribution distribution = null;
	

	public KBRelationStatistics(String prefix, StatisticsType type) {
		this.prefix = prefix;
		this.type = type;
	}

	@Override
	public void begin() {
	}

	@Override
	public void end() {
		ArrayList<Integer> sample = new ArrayList<Integer>();
		for (Entry<Integer, HashSet<Integer>> e : entities.entrySet()) {
			sample.add(e.getValue().size());
			e.getValue().clear();			
		}
		entities.clear();
		distribution = new Distribution(sample);
	}

	public void show() {
	}

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
			if (true) {
				total++;
				if (type == StatisticsType.IN)
					add(o, p);
				if (type == StatisticsType.OUT)
					add(s, p);
			}
		}
	}

	public long getTotal() {
		return total;
	}
	
	public void add(String e, String p) {
		int he = e.hashCode();
		int hp = p.hashCode();
		HashSet<Integer> entity = entities.get(he);
		if (entity == null)
			entity = new HashSet<Integer>();
		entity.add(hp);
		entities.put(he, entity);
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

	public static KBRelationStatistics load(String filename) {
		try {

			FileInputStream fileIn = new FileInputStream(filename);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			Object obj = objectIn.readObject();
			objectIn.close();
			return (KBRelationStatistics) obj;

		} catch (Exception e) {
			return null;
		}
	}
	
	public Distribution getDistribution() {
		return this.distribution;
	}

}
