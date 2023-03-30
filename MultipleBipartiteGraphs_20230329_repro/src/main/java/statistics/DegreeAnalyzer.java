package statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class DegreeAnalyzer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, Integer> uris = new HashMap<Integer, Integer>(); 
	private int maximum = 0;
	private int count = 0;
	private int distinct = 0;
	private String name = null;

	private Distribution distribution;

	public DegreeAnalyzer(String name) {
		this.name = name;		
	}
	
	

	public String getName() {
		return name;
	}



	@SuppressWarnings("deprecation")
	public void add(String uri) {
		//System.out.println(uri + " " + uri.substring(uri.lastIndexOf("/") + 1));
		int h = uri.hashCode();
		Integer counter = uris.get(h);
		if (counter == null) {
			counter = new Integer(0);
			distinct++;
		}
		counter++;
		uris.put(h, counter);
		if (counter > maximum)
			maximum = counter;
		count++;
	}

	public int getMaximum() {
		return maximum;
	}

	public int getCount() {
		return count;
	}

	public int getDistinct() {
		return distinct;
	}

	@Override
	public String toString() {
		return "[ maximum=" + maximum + ", count=" + count + ", distinct=" + distinct  + "]";
	}

	public void free() {
		uris = null;
	}
	
	
	public ArrayList<Integer> getSample() {
		ArrayList<Integer> sample = new ArrayList<Integer>();
		for (Entry<Integer, Integer> hc : uris.entrySet())
			sample.add(hc.getValue());
		return sample;
	}

	public void compute() {
		this.distribution = new Distribution(getSample());
	}

	public Distribution getDistribution() {
		return distribution;
	}

}
