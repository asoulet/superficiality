package statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Distribution implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Integer> values = new ArrayList<Integer>();
	private ArrayList<Integer> counts = new ArrayList<Integer>();
	
	//private int total = 0;
	//private int median = 0;
	
	public Distribution() {		
	}
	
	public Distribution(ArrayList<Integer> sample) {
		Collections.sort(sample);
		int previous = -1;
		int count = 0;
		for (Integer value : sample) {
			if (value != previous) {
				if (previous != -1) {
					values.add(previous);
					counts.add(count);
					count = 0;
				}
				previous = value;					
			}
			count++;
		}
		if (previous != -1) {
			values.add(previous);
			counts.add(count);
			count = 0;
		}
	}
	
	public void show() {
		for (int i = 0; i < values.size(); i++)
			System.out.println(values.get(i) + "\t" + counts.get(i));
	}

	public void apply(DistributionConsumer consumer) {
		consumer.begin();
		for (int i = 0; i < values.size(); i++)
			consumer.add(values.get(i), counts.get(i));
		consumer.end();
	}
	
	public void add(int value, int count) {
		values.add(value);
		counts.add(count);
	}

	public int getMaximumDegree() {
		if (values.size() == 0)
			return 0;
		return values.get(values.size() - 1);
	}
	
	public int size() {
		return values.size();
	}
	
	public int getValue(int i) {
		return values.get(i);
	}
	
	public int getCount(int i) {
		return counts.get(i);
	}
	
	public static void display(ArrayList<Distribution> distributions) {
		int max = 0;
		for (Distribution d : distributions)
			max = Math.max(max, d.size());
		for (int i = 0; i < max; i++) {
			for (Distribution d : distributions)
				if (d.size() > i)
					System.out.print(d.getValue(i) + "\t" + d.getCount(i) + "\t");
				else
					System.out.print("\t\t");		
			System.out.println();
		}
	}
	
	public int getCount() {
		int count = 0;
		for (int i = 0; i < values.size(); i++)
			count += counts.get(i);
		return count;
	}
	
	public int getCumulative() {
		int count = 0;
		for (int i = 0; i < values.size(); i++)
			count += counts.get(i) * values.get(i);
		return count;
	}
	
	public double [] getSample() {
		double [] sample = new double [getCount()];
		int i = 0;
		for (int j = 0; j < values.size(); j++) {
			for (int k = 0; k < counts.get(j); k++) {
				sample[i++] = values.get(j);
			}
		}
		return sample;
	}
	
	public double getKLDivergence(Distribution q) {
		double div = 0;
		double pCount = getCount();
		double qCount = q.getCount();
		int j = 0;
		for (int i = 0; i < size(); i++) {
			while (j < q.size() - 1 && q.getValue(j + 1) <= getValue(i)) {
				j++;
			}
			double pProb = getCount(i) / pCount; 
			double qProb = q.getCount(j) / qCount;
			if (getValue(i) != q.getValue(j)) {
				if (j != q.size() - 1) {
					double rProb = q.getCount(j + 1) / qCount;
					qProb = qProb + (rProb - qProb) * (((double)q.getValue(j + 1)) - getValue(i)) / (((double)q.getValue(j + 1)) - q.getValue(j));
				}
			}
			double v = pProb * Math.log(pProb / qProb);
			div += v;
		}
		return div;
	}
	
	
}
