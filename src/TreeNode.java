/*
 *	Created by Ranjit Kumar Parvathaneni
 *	02/01/2013
 * 
 */

import java.util.*;

class TreeNode {

	public double entropy;
	public Vector<DataPoint> X;
	public Vector<Integer> Y;
	public int selectedAttribute;
	public int selectedAttributeValue;
	public int value;
	public int depth;
	public HashSet<Integer> unProcessedAttributes;
	public TreeNode[] children;
	public TreeNode parent;

	public TreeNode() {
		X = new Vector<DataPoint>();
		Y = new Vector<Integer>();
		unProcessedAttributes = new HashSet<Integer>();
		selectedAttribute = -1;
		value = -1;
	}

};