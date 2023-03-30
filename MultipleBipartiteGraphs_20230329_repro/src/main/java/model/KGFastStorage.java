package model;

import java.util.ArrayList;

import mbg.MBG;
import model.Node.Origin;

public class KGFastStorage implements KGStorage {
	
	private static final float FACTOR = 1.05f;
	private final int [] counters;
	private final Origin [] origins;
	private int maximum = 0;
	
	public KGFastStorage(int distinct) {
		counters = new int [(int) (distinct * FACTOR)];
		origins = new Origin [(int) (distinct * FACTOR)];
	}

	@Override
	public ArrayList<Integer> getSample() {
		ArrayList<Integer> sample = new ArrayList<Integer>();
		for (int i = 0; i <= maximum; i++)
			if (counters[i] != 0)
				sample.add(counters[i]);
		return sample;
	}

	@Override
	public void storeNode(int node, int counter, Origin origin) {
		if (node > maximum)
			maximum = node;
		if (MBG.FACT_COUNTING) 
			counters[node] += counter; // count the number of facts
		else
			counters[node] += 1; // count the number of relations
		if (origins[node] != Origin.NEW)
			origins[node] = origin;
	}

	@Override
	public int getNewNode(int n) {
		for (int i = n; i <= maximum; i++)
			if (counters[i] == 0 || origins[i] == Origin.OTHER) {
				return i;
			}
		return Math.max(maximum + 1, n);
	}

	@Override
	public void show() {
		System.out.println("Entities");
		for (int i = 0; i <= maximum; i++)
			if (counters[i] != 0)
				System.out.println(i + " " + counters[i] + " " + origins[i]);
	}

}
