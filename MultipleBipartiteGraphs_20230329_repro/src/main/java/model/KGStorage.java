package model;

import java.util.ArrayList;
import model.Node.Origin;

public interface KGStorage {
	public ArrayList<Integer> getSample();
	public void storeNode(int node, int counter, Origin origin);
	public int getNewNode(int n);
	public void show();
}
